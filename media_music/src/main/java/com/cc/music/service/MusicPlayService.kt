package com.cc.music.service

import android.content.*
import android.media.*
import android.os.Build
import android.os.IBinder
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.nativeclass.CacheConfig
import com.aliyun.player.source.UrlSource
import com.blankj.utilcode.util.*
import com.cc.ext.logE
import com.cc.ext.logI
import com.cc.music.IMusicCall
import com.cc.music.IMusicOperate
import com.cc.music.bean.MusicBean
import com.cc.music.enu.PlayMode
import com.cc.music.enu.PlayState
import java.io.File

/**
 * http://music.163.com/song/media/outer/url?id=562598065.mp3
 * Author:CASE
 * Date:2020-10-1
 * Time:14:24
 */
class MusicPlayService : AbstractService() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //播放器
  private var aliPlayer = AliPlayerFactory.createAliPlayer(Utils.getApp())

  //是否自动播放
  private var isAutoPlay: Boolean = false

  //当前播放的音乐
  private var mCurrentMusic: MusicBean? = null

  //播放列表
  private var mListMusics = mutableListOf<MusicBean>()

  //播放模式
  private var mPlayMode = PlayMode.LOOP_ALL

  //播放信息回调到外面
  private var mIMusicCall: MutableList<IMusicCall> = mutableListOf()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    //添加监听
    addListener()
    //配置缓存
    aliPlayer.setCacheConfig(getCacheConfig())
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

  //获取播放器缓存配置
  private fun getCacheConfig(): CacheConfig {
    return CacheConfig().apply {
      //开启缓存功能
      mEnable = true
      //能够缓存的单个文件最大时长。超过此长度则不缓存
      mMaxDurationS = 2 * 60 * 60 //2小时
      //缓存目录的位置
      mDir = PathUtils.getExternalAppMusicPath()
      //缓存目录的最大大小。超过此大小，将会删除最旧的缓存文件
      mMaxSizeMB = 2 * 1024 //2GB
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器监听">
  private fun addListener() {
    //播放完成事件
    aliPlayer.setOnCompletionListener {
      callPlayState(PlayState.COMPLETE)
      nextMusic(true)
    }
    //出错事件
    aliPlayer.setOnErrorListener {
      "播放出错:${GsonUtils.toJson(it)}".logE()
      callPlayState(PlayState.ERROR)
    }
    //准备成功事件
    aliPlayer.setOnPreparedListener {
      mCurrentMusic?.let { m -> callMusic(m) }
      callDuration(aliPlayer.duration)
      callPlayState(PlayState.PREPARED)
      if (isAutoPlay) startMusic()
    }
    //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
    aliPlayer.setOnInfoListener { infoBean ->
      //自动播放开始事件(自动播放的时候将不会回调onPrepared，需要从这判断)
      when (infoBean.code) {
        InfoCode.AutoPlayStart -> callPlayState(PlayState.START)
        InfoCode.LoopingStart -> callPlayState(PlayState.START)
        InfoCode.CacheSuccess -> "缓存成功:${mCurrentMusic?.songName ?: ""}".logI()
        InfoCode.CacheError -> if ("url is local source" != infoBean.extraMsg) "缓存失败:${infoBean.extraMsg}".logE()
        InfoCode.SwitchToSoftwareVideoDecoder -> "切换到软解".logE()
        InfoCode.CurrentPosition -> callPlayProgress(infoBean.extraValue)
        InfoCode.BufferedPosition -> callBufferProgress(infoBean.extraValue)
        else -> "当前播放信息infoBean=${GsonUtils.toJson(infoBean)}".logI()
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
    //播放器状态改变事件
    aliPlayer.setOnStateChangedListener {
      when (it) {
        1 -> callPlayState(PlayState.PREPARING)
        2 -> {
          mCurrentMusic?.let { m -> callMusic(m) }
          callPlayState(PlayState.PREPARED)
        }
        3 -> {
          callPlayState(PlayState.BUFFED) //防止loading不显示(没有网络的情况下，会一直在loading，但是连上后开始播放不消失)
          callPlayState(PlayState.START)
        }
        4 -> callPlayState(PlayState.PAUSE)
        5 -> callPlayState(PlayState.STOP)
        6 -> callPlayState(PlayState.COMPLETE)
        7 -> callPlayState(PlayState.ERROR)
        else -> "播放器状态：$it".logI()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器状态回调">
  private var mPlayState = PlayState.SET_DATA
  private var mPlayProgress = 0L
  private var mBufferProgress = 0L
  private fun callPlayState(state: PlayState) {
    if (state != PlayState.SET_DATA && mPlayState == state) return
    "当前播放状态:${state.name}".logI()
    if (state == PlayState.START) {
      if (!hasAudioFocus) requestAudioFocusByVideo()
    } else if (state == PlayState.PAUSE || state == PlayState.COMPLETE || state == PlayState.ERROR || state == PlayState.STOP) {
      if (hasAudioFocus) releaseAudioFocusByVideo()
    }
    mPlayState = state
    mIMusicCall.forEach { it.callPlayState(state.name) }
  }

  private fun callDuration(duration: Long) {
    mIMusicCall.forEach { it.callPlayDuration(duration) }
  }

  private fun callBufferPercent(percent: Int, kbps: Float) {
    mIMusicCall.forEach { it.callBufferPercent(percent, kbps) }
  }

  private fun callPlayProgress(progress: Long) {
    mPlayProgress = progress
    mIMusicCall.forEach { it.callPlayProgress(progress) }
  }

  private fun callBufferProgress(progress: Long) {
    mBufferProgress = progress
    mIMusicCall.forEach { it.callBufferProgress(progress) }
  }

  private fun callMusic(music: MusicBean) {
    mIMusicCall.forEach { it.callMusicInfo(GsonUtils.toJson(music)) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="调用播放器">
  //设置播放列表
  private fun setPlayListMusic(list: MutableList<MusicBean>) {
    mListMusics = list
  }

  //添加播放列表
  private fun addPlayListMusic(list: MutableList<MusicBean>) {
    mListMusics.addAll(list)
  }

  //设置当前播放
  private fun setCurrentPlayMusic(music: MusicBean) {
    setAutoPlayMusic(true)
    mCurrentMusic = music
    aliPlayer.stop()
    //音乐播放地址MD5
    val md5Name = EncryptUtils.encryptMD5ToString(music.url)
    //缓存地址(因为放在一个文件夹会覆盖，所以每首MP3单独一个目录)
    val cacheDir = File(PathUtils.getExternalAppMusicPath(), md5Name)
    if (!cacheDir.exists()) cacheDir.mkdirs()
    //创建歌曲名称
    if (!music.songName.isNullOrBlank()) File(cacheDir, music.songName ?: "").createNewFile()
    //设置缓存目录
    aliPlayer.setCacheConfig(getCacheConfig().apply { mDir = cacheDir.path })
    aliPlayer.setDataSource(UrlSource().apply {
      uri = music.url
      cacheFilePath = File(cacheDir, "${md5Name}.mp3").path
    })
    prepareMusic()
  }

  //设置当前播放
  private fun setCurrentPlayMusic(index: Int) {
    setAutoPlayMusic(true)
    if (mListMusics.size > index) {
      setCurrentPlayMusic(mListMusics[index])
    } else if (mListMusics.isNotEmpty()) {
      setCurrentPlayMusic(mListMusics.first())
    }
  }

  //设置当前播放
  private fun setCurrentPlayMusic(uid: String) {
    setAutoPlayMusic(true)
    val index = mListMusics.indexOfFirst { it.getUid() == uid }
    if (index >= 0) setCurrentPlayMusic(index)
  }

  //设置播放器静音
  fun setMuteMusic(mute: Boolean) {
    aliPlayer.isMute = mute
  }

  //设置自动播放
  private fun setAutoPlayMusic(autoPlay: Boolean) {
    isAutoPlay = autoPlay
  }

  //设置循环播放
  private fun setLoopMusic(loop: Boolean) {
    aliPlayer.isLoop = loop
  }

  //设置循环播放
  private fun setPlayModeMusic(mode: PlayMode) {
    mPlayMode = mode
    mIMusicCall.forEach { it.callPlayMode(mode.name) }
  }

  //准备播放
  private fun prepareMusic() {
    callPlayState(PlayState.PREPARING)
    aliPlayer.prepare()
  }

  //开始播放
  private fun startMusic() {
    if (mCurrentMusic == null && mListMusics.isNotEmpty()) {
      setCurrentPlayMusic(0)
    } else if (mPlayState == PlayState.START) {
      return
    } else if (mCurrentMusic == null || mCurrentMusic?.url.isNullOrBlank()) {
      callPlayState(PlayState.ERROR)
    } else if (mPlayState == PlayState.PREPARING) {
      isAutoPlay = true
      aliPlayer.start()
    } else if (mPlayState == PlayState.PREPARED || mPlayState == PlayState.PAUSE) {
      aliPlayer.start()
      callPlayState(PlayState.START)
    } else if ((mPlayState == PlayState.SET_DATA || mPlayState == PlayState.STOP)) {
      isAutoPlay = true
      prepareMusic()
    }
  }

  //暂停播放
  private fun pauseMusic() {
    if (mPlayState == PlayState.PAUSE) return
    aliPlayer.pause()
    callPlayState(PlayState.PAUSE)
  }

  //上一曲
  private fun previousMusic() {
    if (mListMusics.isNullOrEmpty()) return
    if (mPlayMode == PlayMode.PLAY_RANDOM) {
      setCurrentPlayMusic((Math.random() * mListMusics.size).toInt())
    } else {
      val temp = mCurrentMusic
      setCurrentPlayMusic(
          if (temp == null) 0 else {
            val index = mListMusics.indexOf(temp)
            when {
              index < 0 -> 0
              index == 0 -> mListMusics.size - 1
              else -> index - 1
            }
          }
      )
    }
  }

  //下一曲
  private fun nextMusic(autoNext: Boolean = false) {
    if (mListMusics.isNullOrEmpty()) return
    if (mPlayMode == PlayMode.PLAY_RANDOM) {
      setCurrentPlayMusic((Math.random() * mListMusics.size).toInt())
    } else {
      val temp = mCurrentMusic
      val index = if (temp == null) {
        if (mPlayMode == PlayMode.PLAY_IN_ORDER && autoNext) -1 else 0 //顺序自动播放到没有，则不再播放
      } else {
        val index = mListMusics.indexOf(temp)
        when {
          index < 0 -> 0
          index >= mListMusics.size - 1 -> if (mPlayMode == PlayMode.PLAY_IN_ORDER && autoNext) -1 else 0
          else -> index + 1
        }
      }
      if (index >= 0) setCurrentPlayMusic(index)
    }
  }

  //停止播放
  private fun stopMusic() {
    aliPlayer.stop()
    callPlayState(PlayState.BUFFED)
    callPlayState(PlayState.STOP)
  }

  //跳转到,不精准
  private fun seekToMusic(position: Long) {
    callPlayState(PlayState.SEEKING)
    aliPlayer.seekTo(position)
  }

  //重置
  private fun resetMusic() {
    aliPlayer.reset()
    callPlayState(PlayState.BUFFED)
    callPlayState(PlayState.SET_DATA)
  }

  //释放,释放后播放器将不可再被使用
  private fun releaseMusic() {
    aliPlayer.release()
    callPlayState(PlayState.BUFFED)
    callPlayState(PlayState.SET_DATA)
  }

  private fun getPlayStateMusic(): String = mPlayState.name

  private fun getPlayProgressMusic() = mPlayProgress

  private fun getBufferProgressMusic() = mBufferProgress

  private fun getDurationMusic() = 0L.coerceAtLeast(aliPlayer.duration)

  private fun getMusicInfo(): String? = mCurrentMusic?.run { GsonUtils.toJson(this) }

  private fun getPlayModeMusic() = mPlayMode.name
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="IBinder-外部操作播放器">
  override fun initBinder(): IBinder {
    val pid = android.os.Process.myPid()
    "播放服务PID=$pid".logE()
    return object : IMusicOperate.Stub() {
      override fun setPlayLoop(loop: Boolean) = setLoopMusic(loop)

      override fun setPlayAuto(auto: Boolean) = setAutoPlayMusic(auto)

      override fun setPlayMode(mode: String) = setPlayModeMusic(PlayMode.valueOf(mode))

      override fun setPlayList(list: MutableList<MusicBean>) = setPlayListMusic(list)

      override fun addPlayList(list: MutableList<MusicBean>) = addPlayListMusic(list)

      override fun playStartByIndex(index: Int) = setCurrentPlayMusic(index)

      override fun playStartByUid(uid: String) = setCurrentPlayMusic(uid)

      override fun playStart() = startMusic()

      override fun playPause() = pauseMusic()

      override fun playStop() = stopMusic()

      override fun playRelease() = releaseMusic()

      override fun playPrevious() = previousMusic()

      override fun playNext() = nextMusic()

      override fun playSeekTo(msc: Long) = seekToMusic(msc)

      override fun registerCallback(callback: IMusicCall?) {
        callback?.let { mIMusicCall.add(it) }
      }

      override fun unRegisterCallback(callback: IMusicCall?) {
        callback?.let { mIMusicCall.remove(it) }
      }

      override fun getPlayMode() = getPlayModeMusic()

      override fun getPlayState() = getPlayStateMusic()

      override fun getPlayProgress() = getPlayProgressMusic()

      override fun getBufferProgress() = getBufferProgressMusic()

      override fun getDuration() = getDurationMusic()

      override fun getMusic(): String? = getMusicInfo()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="音频焦点">
  private var mAm = Utils.getApp().getSystemService(Context.AUDIO_SERVICE) as AudioManager
  private var mAudioFocusRequest: AudioFocusRequest? = null
  private var hasAudioFocus = false
  private var audioListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
    hasAudioFocus = focusChange == AudioManager.AUDIOFOCUS_GAIN
    when (focusChange) {
      AudioManager.AUDIOFOCUS_GAIN -> { //获得焦点，这里可以进行恢复播放
        setMuteMusic(false)
        if (mPlayState == PlayState.PAUSE) startMusic()
        "音频焦点监听:GAIN恢复播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { //表示音频焦点请求者需要短暂占有焦点，这里一般需要pause播放
        if (mPlayState == PlayState.START ||
            mPlayState == PlayState.BUFFED ||
            mPlayState == PlayState.SEEKED) pauseMusic()
        "音频焦点监听:LOSS_TRANSIENT暂停播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> { //表示音频焦点请求者需要占有焦点，但是我也可以继续播放，只是需要降低音量或音量置为0
        setMuteMusic(true)
        "音频焦点监听:LOSS_TRANSIENT_CAN_DUCK静音播放".logI()
      }
      AudioManager.AUDIOFOCUS_LOSS -> { //表示音频焦点请求者需要长期占有焦点，这里一般需要stop播放和释放
        stopMusic()
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