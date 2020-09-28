package com.cc.video.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.media.*
import android.os.Build
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
import com.cc.ext.logE
import com.cc.ext.logI
import com.cc.utils.AudioHelper
import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState
import com.cc.video.ext.useMobileNet
import com.cc.video.inter.call.VideoOverCallListener

/**
 * 基于阿里播放器的视频播放控件，实现视频播放和各种回调、控制等。由内部添加的VideoOverView控件管理各种相关控制器
 *
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

  //视频显示控件
  private var mTextureView = TextureView(con)
  private var mSurfaceTexture: SurfaceTexture? = null
  private var mSurface: Surface? = null

  //播放信息
  private var videoUrl: String = ""

  //播放器
  private var aliPlayer = AliPlayerFactory.createAliPlayer(con.applicationContext)

  //缓存
  private var mCacheConfig = CacheConfig()

  //是否处于全屏
  var isFullScreen: Boolean = false

  //是否允许移动网络播放
  private var canUserMobile = Utils.getApp().useMobileNet

  //内部自己判断是否需要自动播放
  private var isAutoPlay = false

  //是否是直播
  private var isLive = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    setBackgroundColor(Color.BLACK)
    addView(mTextureView, 0, ViewGroup.LayoutParams(-1, -1))
    mTextureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
      override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        mSurfaceTexture = surface
        //设置播放的surface
        mSurface = Surface(surface)
        aliPlayer.setSurface(mSurface)
        //重绘界面，立即刷新界面
        aliPlayer.redraw()
      }

      override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
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

  //<editor-fold defaultstate="collapsed" desc="监听播放器相关事件">
  private fun addListener() {
    //播放完成事件
    aliPlayer.setOnCompletionListener {
      if (!aliPlayer.isLoop) {
        callPlayState(PlayState.COMPLETE)
        if (isFullScreen) enterOrExitFullScreen()
      }
    }
    //出错事件
    aliPlayer.setOnErrorListener { callPlayState(PlayState.ERROR) }
    //准备成功事件
    aliPlayer.setOnPreparedListener {
      callDuration(aliPlayer.duration)
      callPlayState(PlayState.PREPARED)
      if (isAutoPlay) startVideo()
    }
    //视频分辨率变化回调
    aliPlayer.setOnVideoSizeChangedListener { width, height -> callVideoSize(width, height) }
    //首帧渲染显示事件
    aliPlayer.setOnRenderingStartListener { callFirstFrame() }
    //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
    aliPlayer.setOnInfoListener { infoBean ->
      //自动播放开始事件(自动播放的时候将不会回调onPrepared，需要从这判断)
      when (infoBean.code) {
        InfoCode.AutoPlayStart -> callPlayState(PlayState.START)
        InfoCode.LoopingStart -> callPlayState(PlayState.START)
        InfoCode.CacheSuccess -> "缓存成功".logI()
        InfoCode.CacheError -> if ("url is local source" != infoBean.extraMsg) "缓存失败:${infoBean.extraMsg}".logE()
        InfoCode.SwitchToSoftwareVideoDecoder -> "切换到软解".logE()
        InfoCode.CurrentPosition -> callPlayProgress(infoBean.extraValue)
        InfoCode.BufferedPosition -> callBufferProgress(infoBean.extraValue)
        else -> "infoBean=${GsonUtils.toJson(infoBean)}".logI()
      }
    }
    //缓冲事件
    aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {

      override fun onLoadingBegin() = callPlayState(PlayState.BUFFING) //缓冲开始

      override fun onLoadingProgress(percent: Int, kbps: Float) = callBufferPercent(percent, kbps) //缓冲进度

      override fun onLoadingEnd() = callPlayState(PlayState.BUFFED) //缓冲结束
    })
    //拖动结束
    aliPlayer.setOnSeekCompleteListener { callPlayState(PlayState.SEEKED) }
    //字幕相关
    aliPlayer.setOnSubtitleDisplayListener(object : IPlayer.OnSubtitleDisplayListener {
      override fun onSubtitleShow(id: Long, data: String) {} //显示字幕

      override fun onSubtitleHide(id: Long) {} //隐藏字幕
    })
    //切换流
    aliPlayer.setOnTrackChangedListener(object : IPlayer.OnTrackChangedListener {
      override fun onChangedSuccess(trackInfo: TrackInfo) {} //切换音视频流或者清晰度成功

      override fun onChangedFail(trackInfo: TrackInfo, errorInfo: ErrorInfo) {} //切换音视频流或者清晰度失败
    })
    //播放器状态改变事件
    aliPlayer.setOnStateChangedListener {
      when (it) {
        1 -> callPlayState(PlayState.PREPARING)
        2 -> callPlayState(PlayState.PREPARED)
        3 -> {
          callPlayState(PlayState.BUFFED) //防止loading不显示(没有网络的情况下，会一直在loading，但是连上后开始播放不消失)
          callPlayState(PlayState.START)
        }
        4 -> callPlayState(PlayState.PAUSE)
        6 -> callPlayState(PlayState.COMPLETE)
        else -> "播放器状态：$it".logI()
      }
    }
    //截图事件
    aliPlayer.setOnSnapShotListener { bm, w, h -> callSnapShot(bm, w, h) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用播放器">
  //设置播放器控件合集View
  fun <T> setOverView(overView: T) where T : View, T : VideoOverCallListener {
    //防止添加多个重复的，所以先移除
    for (i in 0 until childCount) if (getChildAt(i).javaClass.name == overView.javaClass.name) {
      removeViewAt(i)
      break
    }
    addView(overView, ViewGroup.LayoutParams(-1, -1))
    callOutInfo = overView
  }

  //设置是否是直播
  fun setLive(live: Boolean) {
    isLive = live
  }

  //设置缓存相关
  fun setCacheVideo(dsl: (CacheConfig.() -> Unit)? = null) {
    dsl?.invoke(mCacheConfig)
    aliPlayer.setCacheConfig(mCacheConfig)
  }

  //获取缓存文件地址
  fun getCacheFilePath(url: String): String {
    return aliPlayer.getCacheFilePath(url) ?: url
  }

  //设置自动播放
  fun setAutoPlayVideo(autoPlay: Boolean) {
    isAutoPlay = autoPlay
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
    aliPlayer.volume = volume
  }

  //最大音量
  private var maxVolume: Float = 1f

  //设置手机音乐音量
  fun setVolumeVideo(@FloatRange(from = 0.0, to = 1.0) volume: Float) {
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
    videoUrl = url
    "播放地址:$url".logI()
    callVideoInfo(url, title, cover)
    aliPlayer.setDataSource(UrlSource().apply { uri = url })
    if (!checkMobileNet()) prepareVideo()
  }

  //准备播放
  private fun prepareVideo() {
    callPlayState(PlayState.PREPARING)
    aliPlayer.prepare()
  }

  //开始播放
  fun startVideo() {
    if (mPlayState == PlayState.START) {
      return
    } else if (videoUrl.isBlank() || !canNetUse()) {
      callPlayState(PlayState.ERROR)
    } else if (mPlayState == PlayState.PREPARING) {
      isAutoPlay = true
    } else if (mPlayState == PlayState.PREPARED || mPlayState == PlayState.PAUSE) {
      aliPlayer.start()
      callPlayState(PlayState.START)
    } else if ((mPlayState == PlayState.SET_DATA || mPlayState == PlayState.SHOW_MOBILE || mPlayState == PlayState.STOP)
        && !checkMobileNet()) {
      isAutoPlay = true
      prepareVideo()
    }
  }

  //暂停播放
  fun pauseVideo() {
    if (mPlayState == PlayState.PAUSE) return
    aliPlayer.pause()
    callPlayState(PlayState.PAUSE)
  }

  //停止播放
  fun stopVideo() {
    aliPlayer.stop()
    callPlayState(PlayState.STOP)
  }

  //跳转到,不精准
  fun seekToVideo(position: Long) {
    if (isLive) return
    callPlayState(PlayState.SEEKING)
    aliPlayer.seekTo(position)
  }

  //重置
  fun resetVideo() {
    aliPlayer.reset()
    callPlayState(PlayState.SET_DATA)
  }

  //释放,释放后播放器将不可再被使用
  fun releaseVideo() {
    aliPlayer.release()
    callPlayState(PlayState.SET_DATA)
  }

  //进入或者退出全屏
  fun enterOrExitFullScreen() {
    if (isFullScreen) {
      callUiState(PlayUiState.EXIT_FULL)
      if (con is Activity) con.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    } else {
      callUiState(PlayUiState.ENTER_FULL)
      if (con is Activity) con.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
    isFullScreen = !isFullScreen
  }

  //允许手机网络播放
  fun startPlayByModelNet() {
    Utils.getApp().useMobileNet = true
    canUserMobile = true
    startVideo()
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
    if (needResumePlay) startVideo()
    mSurface?.let {
      aliPlayer.setSurface(it)
      aliPlayer.redraw()
    }
  }

  //是否需要回来的时候继续播放
  private var needResumePlay = false

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseVideo() {
    if (mPlayState == PlayState.START) {
      needResumePlay = true
      pauseVideo()
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyVideo() {
    aliPlayer.setSurface(null)
    mSurface?.release()
    mSurfaceTexture?.release()
    releaseVideo()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="对外回调播放信息">
  private var callOutInfo: VideoOverCallListener? = null
  private var mPlayState = PlayState.SET_DATA

  private fun callUiState(uiState: PlayUiState) {
    callOutInfo?.callUiState(uiState)
  }

  private fun callPlayState(state: PlayState) {
    if (state != PlayState.SET_DATA && mPlayState == state) return //防止同样的状态回调多次
    if (state == PlayState.START) {
      if (!hasAudioFocus) requestAudioFocusByVideo()
    } else if (state == PlayState.PAUSE || state == PlayState.COMPLETE || state == PlayState.ERROR || state == PlayState.STOP) {
      if (hasAudioFocus) releaseAudioFocusByVideo()
    }
    mPlayState = state
    callOutInfo?.callPlayState(state)
  }

  private fun callDuration(duration: Long) {
    callOutInfo?.callDuration(duration)
  }

  private fun callPlayProgress(progress: Long) {
    callOutInfo?.callPlayProgress(progress)
  }

  private fun callBufferProgress(progress: Long) {
    callOutInfo?.callBufferProgress(progress)
  }

  private fun callVideoSize(width: Int, height: Int) {
    callOutInfo?.callVideoSize(width, height)
  }

  private fun callFirstFrame() {
    callOutInfo?.callFirstFrame()
  }

  private fun callBufferPercent(percent: Int, kbps: Float) {
    callOutInfo?.callBufferPercent(percent, kbps)
  }

  private fun callSnapShot(bm: Bitmap, width: Int, height: Int) {
    callOutInfo?.callSnapShot(bm, width, height)
  }

  private fun callVideoInfo(url: String, title: String? = "", cover: String? = "") {
    callOutInfo?.callVideoInfo(url, title, cover)
    callPlayState(PlayState.SET_DATA)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手机网络播放判断">
  //检查手机网络状态，如果还没有开放手机网络播放权限，并且使用的是手机网络，则返回true，其余情况返回false
  @SuppressLint("MissingPermission")
  private fun checkMobileNet(): Boolean {
    if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
        && !canUserMobile && NetworkUtils.isConnected() && !NetworkUtils.isWifiConnected()) {
      callPlayState(PlayState.SHOW_MOBILE)
      return true
    }
    return false
  }

  @SuppressLint("MissingPermission")
  private fun canNetUse(): Boolean {
    if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
        && !NetworkUtils.isConnected()) {
      return false
    }
    return true
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="音频焦点">
  private var mAm = con.getSystemService(Context.AUDIO_SERVICE) as AudioManager
  private var mAudioFocusRequest: AudioFocusRequest? = null
  private var hasAudioFocus = false
  private var audioListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
    hasAudioFocus = focusChange == AudioManager.AUDIOFOCUS_GAIN
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> { //获得焦点，这里可以进行恢复播放
        setMuteVideo(false)
        if (mPlayState == PlayState.PAUSE) startVideo()
        "音频焦点监听:GAIN恢复播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { //表示音频焦点请求者需要短暂占有焦点，这里一般需要pause播放
        if (mPlayState == PlayState.START) pauseVideo()
        "音频焦点监听:LOSS_TRANSIENT暂停播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> { //表示音频焦点请求者需要占有焦点，但是我也可以继续播放，只是需要降低音量或音量置为0
        setMuteVideo(true)
        "音频焦点监听:LOSS_TRANSIENT_CAN_DUCK静音播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS -> { //表示音频焦点请求者需要长期占有焦点，这里一般需要stop播放和释放
        stopVideo()
        "音频焦点监听:LOSS停止播放".logI()
      }
    }
  }

  private fun requestAudioFocusByVideo(): Boolean {
    hasAudioFocus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val mAudioAttributes = AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_MEDIA)
          .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
          .build()
      val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
          .setAudioAttributes(mAudioAttributes)
          .setAcceptsDelayedFocusGain(true)
          .setOnAudioFocusChangeListener(audioListener)
          .build()
      mAudioFocusRequest = audioFocusRequest
      mAm.requestAudioFocus(audioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    } else {
      mAm.requestAudioFocus(audioListener,
          AudioManager.STREAM_MUSIC,
          AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
    "请求音频焦点:$hasAudioFocus".logI()
    return hasAudioFocus
  }

  private fun releaseAudioFocusByVideo() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mAudioFocusRequest?.let { mAm.abandonAudioFocusRequest(it) }
    } else {
      mAm.abandonAudioFocus(audioListener)
    }
    hasAudioFocus = false
    "释放音频焦点".logI()
  }
  //</editor-fold>
}