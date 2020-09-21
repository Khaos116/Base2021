package com.cc.video.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.provider.Settings
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.bean.ErrorInfo
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.nativeclass.CacheConfig
import com.aliyun.player.nativeclass.TrackInfo
import com.aliyun.player.source.UrlSource
import com.blankj.utilcode.util.*
import com.cc.utils.AudioHelper
import com.cc.video.ext.*
import com.cc.video.inter.*
import kotlin.math.max
import kotlin.math.min

/**
 * https://help.aliyun.com/document_detail/124714.html?spm=a2c4g.11186623.6.1084.5eddd8e76YpCGt
 * Author:CASE
 * Date:2020-9-16
 * Time:15:37
 */
class AliVideoView @JvmOverloads constructor(
  private val con: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : FrameLayout(con, attrs, defStyleAttr, defStyleRes), LifecycleObserver {

  //<editor-fold defaultstate="collapsed" desc="变量区">
  //Over控件
  private var mOverParent: VideoOverView = VideoOverView(con)

  //视频显示控件
  private var mTextureView = TextureView(con)
  private var mSurfaceTexture: SurfaceTexture? = null
  private var mSurface: Surface? = null

  //播放信息
  private var videoUrl: String = ""
  private var videoTitle: String = ""
  private var videoCover: String = ""

  //播放器
  private var aliPlayer = AliPlayerFactory.createAliPlayer(con.applicationContext)

  //缓存
  private var mCacheConfig = CacheConfig()

  //是否在播放
  private var isPlaying = false

  //是否处于暂停
  private var isPause = false

  //是否处于全屏
  private var isFullScreen: Boolean = false

  //是否允许移动网络播放
  private var canUserMobile = Utils.getApp().useMobileNet

  //是否显示了异常View
  private var isShowError = false

  //当前播放位置
  private var mCurrentPosition = 0L
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="回调状态到控制器">
  //回调Controller状态
  private var callController: VideoControllerCallListener? = null

  //回调Loading状态
  private var callLoading: VideoLoadingCallListener? = null

  //回调Error状态
  private var callError: VideoErrorCallListener? = null

  //回调手势状态
  private var callGesture: VideoGestureCallListener? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    setBackgroundColor(Color.BLACK)
    addView(mTextureView, 0, ViewGroup.LayoutParams(-1, -1))
    addView(mOverParent, ViewGroup.LayoutParams(-1, -1))
    mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
      override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
      ) {
        mSurfaceTexture = surface
        //设置播放的surface
        mSurface = Surface(surface)
        aliPlayer.setSurface(mSurface)
        //重绘界面，立即刷新界面
        aliPlayer.redraw()
      }

      override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
      ) {
        //画面大小变化的时候重绘界面，立即刷新界面
        aliPlayer.redraw()
      }

      //这里一定要return false。这样SurfaceTexture不会被系统销毁
      override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = false

      override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }
    //添加监听
    addListener()
    //配置缓存
    mCacheConfig.apply {
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
      if (!aliPlayer.isLoop) resetVideo()
    }
    //出错事件
    aliPlayer.setOnErrorListener { callPlayError() }
    //准备成功事件
    aliPlayer.setOnPreparedListener {
      callLoading?.hiddenLoading()
      if (!isShowError) {
        callController?.callDuration(aliPlayer.duration)
        setCanOperate(true)
      }
    }
    //视频分辨率变化回调
    aliPlayer.setOnVideoSizeChangedListener { width, height -> }
    //首帧渲染显示事件
    aliPlayer.setOnRenderingStartListener {
      if (!isShowError) {
        callController?.callDuration(aliPlayer.duration)
        setCanOperate(true)
      }
    }
    //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
    aliPlayer.setOnInfoListener { infoBean ->
      //自动播放开始事件(自动播放的时候将不会回调onPrepared，需要从这判断)
      when (infoBean.code) {
        InfoCode.AutoPlayStart -> {
          isPlaying = true
          isPause = false
          callLoading?.hiddenLoading()
        }
        InfoCode.LoopingStart -> {
          isPlaying = true
          isPause = false
        }
        InfoCode.CacheSuccess -> "缓存成功".logI()
        InfoCode.CacheError -> if ("url is local source" != infoBean.extraMsg) "缓存失败:${infoBean.extraMsg}".logE()
        InfoCode.SwitchToSoftwareVideoDecoder -> "切换到软解".logE()
        InfoCode.CurrentPosition -> {
          mCurrentPosition = infoBean.extraValue
          callController?.callProgress(mCurrentPosition)
        }
        InfoCode.BufferedPosition -> callController?.callBufferProgress(infoBean.extraValue)
        else -> {
          "infoBean=${GsonUtils.toJson(infoBean)}".logI()
        }
      }
    }
    //缓冲事件
    aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
      override fun onLoadingBegin() { //缓冲开始
        if (!isShowError) callLoading?.showLoading()
      }

      override fun onLoadingProgress(percent: Int, kbps: Float) { //缓冲进度
        callLoading?.showBuffer(percent, kbps)
      }

      override fun onLoadingEnd() { //缓冲结束
        callLoading?.hiddenLoading()
      }
    })
    //拖动结束
    aliPlayer.setOnSeekCompleteListener {

    }
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
        3 -> {
          callLoading?.hiddenLoading()
          callController?.callPlay()
        }
        4 -> callController?.callPause()
        6 -> callController?.callComplete()
        else -> "播放器状态：$it".logI()
      }
    }
    //截图事件
    aliPlayer.setOnSnapShotListener { bm, with, height ->

    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放出错判断(主要处理手机网络播放状态)">
  @SuppressLint("MissingPermission")
  private fun callPlayError() {
    if (callError == null) return
    isShowError = true
    if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_NETWORK_STATE)
      == PackageManager.PERMISSION_GRANTED
    ) {
      if (!NetworkUtils.isConnected()) { //无网络
        callError?.errorNoNet()
      } else if (NetworkUtils.isWifiConnected()) { //Wifi状态
        callError?.errorNormal()
      } else if (!canUserMobile) { //手机网络，但是不允许使用
        callError?.errorMobileNet()
      } else {
        isShowError = false
      }
    } else {
      callError?.errorNormal()
    }
    callLoading?.hiddenLoading()
    if (isShowError) setCanOperate(false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用播放器">
  //设置缓存相关
  fun setCacheVideo(dsl: (CacheConfig.() -> Unit)? = null) {
    dsl?.invoke(mCacheConfig)
  }

  //获取缓存文件地址
  fun getCacheFilePath(url: String): String {
    return aliPlayer.getCacheFilePath(url) ?: url
  }

  //设置自动播放
  fun setAutoPlayVideo(autoPlay: Boolean) {
    aliPlayer.isAutoPlay = autoPlay
  }

  //设置循环播放
  fun setLoopVideo(loop: Boolean) {
    aliPlayer.isLoop = loop
  }

  //设置画面的镜像模式：水平镜像，垂直镜像，无镜像
  fun setMirrorVideo(mode: IPlayer.MirrorMode) {
    aliPlayer.mirrorMode = mode
  }

  //设置画面旋转模式：旋转0度，90度，180度，270度
  fun setRotateVideo(mode: IPlayer.RotateMode) {
    aliPlayer.rotateMode = mode
  }

  //设置画面缩放模式：宽高比填充，宽高比适应，拉伸填充
  fun setScaleVideo(mode: IPlayer.ScaleMode) {
    aliPlayer.scaleMode = mode
  }

  //设置播放器静音
  fun setMuteVideo(mute: Boolean) {
    aliPlayer.isMute = mute
  }

  //设置播放器音量,范围0~1
  fun setVolumePlayerVideo(@FloatRange(from = 0.0, to = 1.0) volume: Float) {
    "播放器音量:$volume".logI()
    aliPlayer.volume = volume
  }

  //最大音量
  private var maxVolume: Float = 1f

  //设置手机音乐音量
  fun setVolumeVideo(volume: Float) {
    "手机音乐音量:$volume".logI()
    maxVolume = AudioHelper.getInstance().musicMaxVolume * 1f
    AudioHelper.getInstance().setVolume(AudioManager.STREAM_MUSIC, (volume * maxVolume).toInt(), false)
  }

  //设置倍速播放:支持0.5~2倍速的播放
  fun setSpeedVideo(@FloatRange(from = 0.5, to = 2.0) speed: Float) {
    aliPlayer.speed = speed
  }

  //截取当前播放的画面
  fun snapShotVideo() {
    aliPlayer.snapshot()
  }

  //设置播放地址
  fun setUrlVideo(url: String, title: String? = "", cover: String? = "") {
    "播放地址:$url".logI()
    setCanOperate(false)
    callController?.callStop()
    videoUrl = url
    aliPlayer.setDataSource(UrlSource().apply { uri = url })
    videoTitle = title ?: ""
    callController?.callTitle(videoTitle)
    videoCover = cover ?: ""
  }

  //准备播放
  @SuppressLint("MissingPermission")
  fun prepareVideo() {
    aliPlayer.prepare()
    callController?.callPrepare()
    if (ContextCompat.checkSelfPermission(
        con,
        Manifest.permission.ACCESS_NETWORK_STATE
      ) == PackageManager.PERMISSION_GRANTED
    ) {
      if (callError != null)
        if (!canUserMobile && NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected()) {
          aliPlayer.isAutoPlay = false
          isShowError = true
          callError?.errorMobileNet()
          callLoading?.hiddenLoading()
          setCanOperate(false)
        } else if (!NetworkUtils.isConnected()) {
          aliPlayer.isAutoPlay = false
          isShowError = true
          callError?.errorNoNet()
          callLoading?.hiddenLoading()
          setCanOperate(false)
        }
    }
    isPlaying = aliPlayer.isAutoPlay
    isPause = !isPlaying
  }

  //准备并播放
  fun prepareStartVideo() {
    prepareVideo()
    startVideo()
  }

  //开始播放
  @SuppressLint("MissingPermission")
  fun startVideo() {
    if (ContextCompat.checkSelfPermission(
        con,
        Manifest.permission.ACCESS_NETWORK_STATE
      ) == PackageManager.PERMISSION_GRANTED &&
      callError != null
    ) {
      if (!canUserMobile && NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected()) {
        isShowError = true
        callError?.errorMobileNet()
        callLoading?.hiddenLoading()
        setCanOperate(false)
        return
      } else if (!NetworkUtils.isConnected()) {
        isShowError = true
        callError?.errorNoNet()
        callLoading?.hiddenLoading()
        setCanOperate(false)
        return
      }
    }
    if (isPlaying) return
    isPlaying = true
    isPause = false
    aliPlayer.start()
    callController?.callPlay()
  }

  //暂停播放
  fun pauseVideo() {
    if (isPause) return
    isPause = true
    isPlaying = false
    aliPlayer.pause()
    callController?.callPause()
  }

  //停止播放
  fun stopVideo() {
    aliPlayer.stop()
    callController?.callStop()
    resetVideo()
  }

  //跳转到,不精准
  fun seekToVideo(position: Long) {
    aliPlayer.seekTo(position)
  }

  //重置
  fun resetVideo() {
    callLoading?.hiddenLoading()
    aliPlayer.reset()
    prepareVideo()
  }

  //释放,释放后播放器将不可再被使用
  fun releaseVideo() {
    aliPlayer.release()
    isPause = false
    isPlaying = false
  }

  //进入或者退出全屏
  fun enterOrExitFullScreen() {
    if (isFullScreen) {
      callController?.exitFullScreen()
      if (con is Activity) con.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
      callController?.enterFullScreen()
      if (con is Activity) con.requestedOrientation =
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    isFullScreen = !isFullScreen
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeVideo() {
    "onResumeVideo".logI()
    if (isPause) startVideo()
    mSurface?.let {
      aliPlayer.setSurface(it)
      aliPlayer.redraw()
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseVideo() {
    "onPauseVideo".logI()
    pauseVideo()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyVideo() {
    aliPlayer.setSurface(null)
    mSurface?.release()
    mSurfaceTexture?.release()
    releaseVideo()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加上层(控制器)控件">
  fun addOverView(over: View) {
    mOverParent.addOverChildView(over)
    //播放组件相关
    when (over) {
      is VideoControllerCallListener -> {
        over.setCall(operateController)
        callController = over
      }
      is VideoLoadingCallListener -> {
        callLoading = over
      }
      is VideoErrorCallListener -> {
        over.setCall(operateError)
        callError = over
      }
      is VideoGestureCallListener -> {
        over.setCall(operateGesture)
        callGesture = over
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="控制器操作的监听">
  //正常控制
  private var operateController = object : VideoControllerListener {
    override fun onBack() {
      if (isFullScreen) {
        enterOrExitFullScreen()
      } else {
        if (con is Activity) con.onBackPressed()
      }
    }

    override fun onPlayOrPause() {
      if (isPause) {
        startVideo()
      } else if (isPlaying) {
        pauseVideo()
      }
    }

    override fun onPlay() {
      startVideo()
    }

    override fun onPause() {
      pauseVideo()
    }

    override fun seekTo(msc: Long) {
      seekToVideo(msc)
    }

    override fun onStop() {
      stopVideo()
    }

    override fun fullScreenOrExit() {
      enterOrExitFullScreen()
    }
  }

  //异常控制
  private var operateError = object : VideoErrorListener {
    override fun continuePlay() {
      isShowError = false
      canUserMobile = true
      Utils.getApp().useMobileNet = true
      if (aliPlayer.duration > 0) {
        callController?.callDuration(aliPlayer.duration)
        setCanOperate(true)
      }
      startVideo()
    }

    override fun resetPlay() {
      isShowError = false
      resetVideo()
      startVideo()
    }
  }

  //手势操作
  private var operateGesture = object : VideoGestureListener {
    override fun getDuration(): Long {
      return if (videoUrl.isNotBlank()) aliPlayer.duration else 0
    }

    override fun getCurrentPosition(): Long {
      return mCurrentPosition
    }

    override fun getVolumeCurrent(): Float {
      return AudioHelper.getInstance().getCurrentVolume(AudioManager.STREAM_MUSIC) * 1f
    }

    override fun getVolumeMax(): Float {
      return AudioHelper.getInstance().musicMaxVolume * 1f
    }

    override fun getVolumeMin(): Float {
      return 0f
    }

    override fun getBrightCurrent(): Float {
      if (con is Activity) {
        val value = con.window.attributes.screenBrightness
        return if (value < 0) {
          max(0f, Settings.System.getInt(con.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f)
        } else {
          value
        }
      }
      return 1f
    }

    override fun getBrightMax(): Float {
      return 1f
    }

    override fun getBrightMin(): Float {
      return 0.01f
    }

    override fun onPause() {
      pauseVideo()
    }

    override fun onStart() {
      startVideo()
    }

    override fun seekPreviewTo(msc: Long) {
      callController?.callProgress(msc)
    }

    override fun seekTo(msc: Long) {
      seekToVideo(msc)
    }

    override fun setPlayerVolume(volume: Float) {
      setVolumePlayerVideo(volume)
    }

    override fun setVolume(volume: Float) {
      setVolumeVideo(volume)
    }

    override fun setBright(bright: Float) {
      if (con is Activity) con.window.apply {
        attributes.screenBrightness = max(0.01f, min(1f, bright))
        con.window.attributes = attributes
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="回调到控制器判断是否可以操作">
  private fun setCanOperate(can: Boolean) {
    callController?.callOperate(can)
    callGesture?.callOperate(can)
  }
  //</editor-fold>
}