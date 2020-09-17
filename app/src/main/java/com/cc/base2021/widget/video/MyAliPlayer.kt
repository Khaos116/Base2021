package com.cc.base2021.widget.video

import android.view.Surface
import android.view.SurfaceHolder
import com.aliyun.player.*
import com.aliyun.player.bean.ErrorInfo
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.nativeclass.CacheConfig
import com.aliyun.player.nativeclass.TrackInfo
import com.aliyun.player.source.UrlSource
import com.blankj.utilcode.util.*
import com.cc.base.ext.logE
import com.cc.base.ext.logI
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.entity.DecoderPlan
import com.kk.taurus.playerbase.event.*
import com.kk.taurus.playerbase.player.BaseInternalPlayer
import kotlin.math.max
import kotlin.math.min

/**
 * 参考 https://github.com/jiajunhui/PlayerBase/blob/master/ijkplayer/src/main/java/com/kk/taurus/ijkplayer/IjkPlayer.java
 * Author:CASE
 * Date:2020-9-17
 * Time:12:50
 */
class MyAliPlayer : BaseInternalPlayer() {
  //<editor-fold defaultstate="collapsed" desc="外部设置播放器内核">
  companion object {
    private const val PLAN_ID = 1001
    fun init() {
      PlayerConfig.addDecoderPlan(DecoderPlan(PLAN_ID, MyAliPlayer::class.java.name, "aliPlayer"))
      PlayerConfig.setDefaultPlanId(PLAN_ID)
      PlayerLibrary.init(Utils.getApp())
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //阿里播放器
  private var aliPlayer: AliPlayer = AliPlayerFactory.createAliPlayer(Utils.getApp())

  //当前播放位置
  private var mCurrentPosition: Long = 0

  //缓冲位置
  private var mBufferPosition: Long = 0

  //是否正在播放
  private var isPlaying = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    //配置缓存
    CacheConfig().apply {
      //开启缓存功能
      mEnable = true
      //能够缓存的单个文件最大时长。超过此长度则不缓存
      mMaxDurationS = 2 * 60 * 60 //2小时
      //缓存目录的位置
      mDir = PathUtils.getExternalAppMoviesPath()
      //缓存目录的最大大小。超过此大小，将会删除最旧的缓存文件
      mMaxSizeMB = 2 * 1024 //2GB
      //设置缓存配置给到播放器
      aliPlayer.setCacheConfig(this)
    }
    //开启硬解，默认开启(硬解初始化失败时，自动切换为软解，保证视频的正常播放)
    aliPlayer.enableHardwareDecoder(true)
    //网络重试时间和次数
    aliPlayer.config.apply {
      //设置网络超时时间，单位ms
      mNetworkTimeout = 30 * 1000 //30秒
      //设置超时重试次数。每次重试间隔为networkTimeout。networkRetryCount=0则表示不重试，重试策略app决定，默认值为2
      mNetworkRetryCount = 3 //重试3次
      //设置配置给播放器
      aliPlayer.config = this
    }
    //配置缓存和延迟控制(三个缓冲区时长的大小关系必须为：mStartBufferDuration<=mHighBufferDuration<=mMaxBufferDuration)
    aliPlayer.config.apply {
      //最大延迟。注意：直播有效。当延时比较大时，播放器sdk内部会追帧等，保证播放器的延时在这个范围内
      mMaxDelayTime = 10 * 1000 //10秒
      //最大缓冲区时长。单位ms。播放器每次最多加载这么长时间的缓冲数据
      mMaxBufferDuration = 30 * 1000 //30秒
      //高缓冲时长。单位ms。当网络不好导致加载数据时，如果加载的缓冲时长到达这个值，结束加载状态
      mHighBufferDuration = 5 * 1000 //5秒
      //起播缓冲区时长。单位ms。这个时间设置越短，起播越快。也可能会导致播放之后很快就会进入加载状态
      mStartBufferDuration = 1 * 1000 //1秒
      //设置配置给播放器
      aliPlayer.config = this
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="监听">
  private fun addListener() {
    //播放完成事件
    aliPlayer.setOnCompletionListener {
      updateStatus(STATE_PLAYBACK_COMPLETE)
      submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE, null)
      if (!isLooping) isPlaying = false
    }
    //出错事件
    aliPlayer.setOnErrorListener {
      updateStatus(STATE_ERROR)
      val bundle = BundlePool.obtain()
      submitErrorEvent(OnErrorEventListener.ERROR_EVENT_COMMON, bundle)
    }
    //准备成功事件
    aliPlayer.setOnPreparedListener {
      updateStatus(STATE_PREPARED)
      val bundle = BundlePool.obtain()
      bundle.putInt(EventKey.INT_ARG1, aliPlayer.videoWidth)
      bundle.putInt(EventKey.INT_ARG2, aliPlayer.videoHeight)
      submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED, bundle)
    }
    //视频分辨率变化回调
    aliPlayer.setOnVideoSizeChangedListener { width, height ->
      val bundle = BundlePool.obtain()
      bundle.putInt(EventKey.INT_ARG1, width)
      bundle.putInt(EventKey.INT_ARG2, height)
      bundle.putInt(EventKey.INT_ARG3, 1)
      bundle.putInt(EventKey.INT_ARG4, 1)
      submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_SIZE_CHANGE, bundle)
    }
    //首帧渲染显示事件
    aliPlayer.setOnRenderingStartListener { submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START, null) }
    //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
    aliPlayer.setOnInfoListener { infoBean ->
      //自动播放开始事件(自动播放的时候将不会回调onPrepared，需要从这判断)
      when (infoBean.code) {
        InfoCode.AutoPlayStart -> isPlaying = true
        InfoCode.LoopingStart -> isPlaying = true
        InfoCode.Unknown -> isPlaying = false
        InfoCode.AudioDecoderDeviceError -> isPlaying = false
        InfoCode.VideoDecoderDeviceError -> isPlaying = false
        InfoCode.AudioCodecNotSupport -> isPlaying = false
        InfoCode.VideoCodecNotSupport -> isPlaying = false
        InfoCode.CacheSuccess -> "缓存成功".logI()
        InfoCode.CacheError -> if ("url is local source" != infoBean.extraMsg) "缓存失败:${infoBean.extraMsg}".logE()
        InfoCode.SwitchToSoftwareVideoDecoder -> "切换到软解".logE()
        InfoCode.CurrentPosition -> mCurrentPosition = infoBean.extraValue
        InfoCode.BufferedPosition -> mBufferPosition = infoBean.extraValue
        else -> "infoBean=${GsonUtils.toJson(infoBean)}".logI()
      }
    }
    //缓冲事件
    aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
      override fun onLoadingBegin() { //缓冲开始
        submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START, null)
      }

      override fun onLoadingProgress(percent: Int, kbps: Float) { //缓冲进度
        submitBufferingUpdate(percent, null)
      }

      override fun onLoadingEnd() { //缓冲结束
        submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END, null)
      }
    })
    //拖动结束
    aliPlayer.setOnSeekCompleteListener { submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE, null) }
    //字幕相关
    aliPlayer.setOnSubtitleDisplayListener(object : IPlayer.OnSubtitleDisplayListener {
      override fun onSubtitleShow(id: Long, data: String) { //显示字幕
      }

      override fun onSubtitleHide(id: Long) { //隐藏字幕
      }

    })
    //切换流
    aliPlayer.setOnTrackChangedListener(object : IPlayer.OnTrackChangedListener {
      override fun onChangedSuccess(trackInfo: TrackInfo) { //切换音视频流或者清晰度成功
      }

      override fun onChangedFail(trackInfo: TrackInfo, errorInfo: ErrorInfo) { //切换音视频流或者清晰度失败
      }
    })
    //播放器状态改变事件
    aliPlayer.setOnStateChangedListener {
      when (it) {
        3 -> isPlaying = true
        4 -> isPlaying = false
        6 -> isPlaying = isLooping
        else -> "播放器状态：$it".logI()
      }
    }
    //截图事件
    aliPlayer.setOnSnapShotListener { bm, with, height ->

    }
  }

  //清空监听
  private fun resetListener() {
    //播放完成事件
    aliPlayer.setOnCompletionListener(null)
    //出错事件
    aliPlayer.setOnErrorListener(null)
    //准备成功事件
    aliPlayer.setOnPreparedListener(null)
    //视频分辨率变化回调
    aliPlayer.setOnVideoSizeChangedListener(null)
    //首帧渲染显示事件
    aliPlayer.setOnRenderingStartListener(null)
    //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
    aliPlayer.setOnInfoListener(null)
    //缓存事件
    aliPlayer.setOnLoadingStatusListener(null)
    //拖动结束
    aliPlayer.setOnSeekCompleteListener(null)
    //字幕相关
    aliPlayer.setOnSubtitleDisplayListener(null)
    //切换流
    aliPlayer.setOnTrackChangedListener(null)
    //播放器状态改变事件
    aliPlayer.setOnStateChangedListener(null)
    //截图事件
    aliPlayer.setOnSnapShotListener(null)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="重写方法">
  override fun setDataSource(dataSource: DataSource?) {
    addListener()
    dataSource?.data?.let { url ->
      aliPlayer.setDataSource(UrlSource().apply { uri = url })
      aliPlayer.prepare()
    }
    val bundle = BundlePool.obtain()
    bundle.putSerializable(EventKey.SERIALIZABLE_DATA, dataSource)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET, bundle)
  }

  override fun setDisplay(surfaceHolder: SurfaceHolder?) {
    aliPlayer.setDisplay(surfaceHolder)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_HOLDER_UPDATE, null)
  }

  override fun setSurface(surface: Surface?) {
    aliPlayer.setSurface(surface)
    //重绘界面，立即刷新界面
    aliPlayer.redraw()
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SURFACE_UPDATE, null)
  }

  override fun setVolume(left: Float, right: Float) {
    aliPlayer.volume = min(1f, max(0f, max(left, right)))
  }

  override fun setSpeed(speed: Float) {
    aliPlayer.speed = min(2f, max(0.5f, speed))
  }

  override fun isPlaying(): Boolean {
    return isPlaying
  }

  override fun getCurrentPosition(): Int {
    return mCurrentPosition.toInt()
  }

  override fun getDuration(): Int {
    return aliPlayer.duration.toInt()
  }

  override fun getAudioSessionId(): Int {
    return 0
  }

  override fun getVideoWidth(): Int {
    return aliPlayer.videoWidth
  }

  override fun getVideoHeight(): Int {
    return aliPlayer.videoHeight
  }

  override fun start() {
    aliPlayer.start()
    updateStatus(STATE_STARTED)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_START, null)
  }

  override fun start(msc: Int) {
    aliPlayer.seekTo(msc.toLong())
    aliPlayer.start()
  }

  override fun pause() {
    aliPlayer.pause()
    updateStatus(STATE_PAUSED)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE, null)
  }

  override fun resume() {
    aliPlayer.start()
    updateStatus(STATE_STARTED)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESUME, null)
  }

  override fun seekTo(msc: Int) {
    aliPlayer.seekTo(msc.toLong())
    val bundle = BundlePool.obtain()
    bundle.putInt(EventKey.INT_DATA, msc)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO, bundle)
  }

  override fun stop() {
    aliPlayer.stop()
    updateStatus(STATE_STOPPED)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_STOP, null)
  }

  override fun reset() {
    aliPlayer.reset()
    updateStatus(STATE_IDLE)
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_RESET, null)
  }

  override fun destroy() {
    resetListener()
    aliPlayer.release()
    submitPlayerEvent(OnPlayerEventListener.PLAYER_EVENT_ON_DESTROY, null)
  }

  override fun setLooping(looping: Boolean) {
    super.setLooping(looping)
    aliPlayer.isLoop = isLooping
  }
  //</editor-fold>
}