package com.cc.video.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.provider.Settings.System
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.blankj.utilcode.util.Utils
import com.cc.ext.logE
import com.cc.ext.removeParent
import com.cc.ext.visibleGone
import com.cc.utils.AudioHelper
import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState
import com.cc.video.ext.useMobileNet
import com.cc.video.inter.OverGestureListener
import com.cc.video.inter.call.*
import com.cc.video.inter.operate.*
import kotlin.math.max
import kotlin.math.min

/**
 * 所有控制器相关View添加到这个父容器，最主要的目的是分发手势操作
 * Author:CASE
 * Date:2020-9-21
 * Time:14:51
 */
open class VideoOverView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(con, attrs, defStyleAttr, defStyleRes), VideoOverCallListener {
  //<editor-fold defaultstate="collapsed" desc="获取子View">
  private var mCacheList = mutableListOf<View>()
  private fun getAllChildView(readCache: Boolean = false): MutableList<View> {
    if (readCache) return mCacheList
    val temp = mutableListOf<View>()
    for (i in 0 until childCount) temp.add(getChildAt(i))
    mCacheList = temp
    return temp
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势监听">
  private var mGestureDetector: GestureDetector? = null
  private var simpleOnGestureListener: GestureDetector.SimpleOnGestureListener =
      object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
          getAllChildView().filter { it is OverGestureListener }
              .forEach { (it as OverGestureListener).callOverClick() }
          return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
          getAllChildView().filter { it is OverGestureListener }
              .forEach { (it as OverGestureListener).callOverDoubleClick() }
          return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
          getAllChildView().filter { it is OverGestureListener }
              .forEach { (it as OverGestureListener).callOverTouchDown(e) }
          return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
          getAllChildView(true).filter { it is OverGestureListener }.forEach {
            (it as OverGestureListener).callOverScroll(e1, e2, distanceX, distanceY)
          }
          return true
        }
      }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    mGestureDetector = GestureDetector(con, simpleOnGestureListener)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Touch事件">
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent?): Boolean {
    mGestureDetector?.let { gd ->
      gd.onTouchEvent(event)
      if (event?.action ?: 0 == MotionEvent.ACTION_UP) {
        getAllChildView().filter { it is OverGestureListener }
            .forEach { (it as OverGestureListener).callOverTouchUp() }
      }
      return true
    }
    return super.onTouchEvent(event)
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取播放View">
  private fun getVideoView(): AliVideoView? {
    parent?.let { if (it is AliVideoView) return it }
    return null
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加播放控制器相关View">
  fun addOverChildView(over: View) {
    //如果已经存在一样的，则移除之前的
    getAllChildView().firstOrNull { it.javaClass.name == over.javaClass.name }?.removeParent()
    over.removeParent()
    if (over is VideoControllerCallListener) {
      addView(over, 0, ViewGroup.LayoutParams(-1, -1))
    } else {
      addView(over, ViewGroup.LayoutParams(-1, -1))
    }
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
      is VideoCompleteCallListener -> {
        over.setCall(operateComplete)
        callComplete = over
      }
      is VideoCoverCallListener -> {
        over.setCall(operateCover)
        callCover = over
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="控制器操作的监听">
  //正常控制
  private var operateController = object : VideoControllerListener {
    override fun onBack() {
      val videoView = getVideoView()
      if (videoView != null && videoView.isFullScreen) {
        videoView.enterOrExitFullScreen()
      } else if (con is Activity) con.onBackPressed()
    }

    override fun onPlayOrPause() {
      if (mPlayState == PlayState.PAUSE) {
        getVideoView()?.startVideo()
      } else if (mPlayState == PlayState.START) {
        getVideoView()?.pauseVideo()
      }
    }

    override fun onPlay() {
      getVideoView()?.startVideo()
    }

    override fun onPause() {
      getVideoView()?.pauseVideo()
    }

    override fun seekTo(msc: Long) {
      getVideoView()?.seekToVideo(msc)
    }

    override fun onStop() {
      getVideoView()?.stopVideo()
    }

    override fun lock() {
      callGesture?.callUiState(PlayUiState.LOCK_SCREEN)
    }

    override fun unlock() {
      callGesture?.callUiState(PlayUiState.UNLOCK_SCREEN)
    }

    override fun fullScreenOrExit() {
      getVideoView()?.enterOrExitFullScreen()
    }

    override fun refreshPlay() {
      getVideoView()?.let {
        it.resetVideo()
        it.startVideo()
      }
    }
  }

  //异常控制
  private var operateError = object : VideoErrorListener {
    override fun continuePlay() {
      getVideoView()?.startPlayByModelNet()
    }

    override fun resetPlay() {
      getVideoView()?.let {
        it.resetVideo()
        it.startVideo()
      }
    }
  }

  //手势操作
  private var operateGesture = object : VideoGestureListener {
    override fun getDuration(): Long {
      return mDuration
    }

    override fun getCurrentPosition(): Long {
      return mPlayProgress
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
          max(0f, System.getInt(con.getContentResolver(), System.SCREEN_BRIGHTNESS) / 255f)
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
      getVideoView()?.pauseVideo()
    }

    override fun onStart() {
      getVideoView()?.startVideo()
    }

    override fun seekPreviewTo(msc: Long) {
    }

    override fun seekTo(msc: Long) {
      getVideoView()?.seekToVideo(msc)
    }

    override fun setPlayerVolume(volume: Float) {
      getVideoView()?.setVolumePlayerVideo(volume)
    }

    override fun setVolume(volume: Float) {
      getVideoView()?.setVolumeVideo(volume)
    }

    override fun setBright(bright: Float) {
      if (con is Activity) con.window.apply {
        attributes.screenBrightness = max(0.01f, min(1f, bright))
        con.window.attributes = attributes
      }
    }
  }

  //播放结束的重新播放
  private var operateComplete = object : VideoCompleteListener {
    override fun resetPlay() {
      getVideoView()?.let {
        it.resetVideo()
        it.startVideo()
      }
    }
  }

  //开始播放的操作
  private var operateCover = object : VideoCoverListener {
    override fun startPlay() {
      getVideoView()?.startVideo()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器对外回调">
  private var mPlayState: PlayState = PlayState.SET_DATA
  private var mDuration: Long = 0
  private var mPlayProgress: Long = 0
  override fun callVideoInfo(url: String, title: String?, cover: String?) {
    callController?.callVideoInfo(url, title, cover)
    callCover?.callVideoInfo(url, title, cover)
  }

  override fun callDuration(duration: Long) {
    mDuration = duration
    callController?.callDuration(duration)
  }

  override fun callFirstFrame() {
  }

  override fun callVideoSize(width: Int, height: Int) {
  }

  override fun callUiState(uiState: PlayUiState) {
    callGesture?.callUiState(uiState)
    callController?.callUiState(uiState)
  }

  override fun callPlayState(playState: PlayState) {
    "PlayState=${playState}".logE()
    mPlayState = playState
    //回调状态
    callComplete?.callPlayState(playState)
    callController?.callPlayState(playState)
    callCover?.callPlayState(playState)
    callError?.callPlayState(playState)
    callGesture?.callPlayState(playState)
    callLoading?.callPlayState(playState)
    //判断显示和隐藏
    this.visibleGone(playState != PlayState.SET_DATA)
    when (playState) {
      PlayState.ERROR -> setOverError()
      PlayState.PREPARING -> setOverCover()
      PlayState.SHOW_MOBILE -> setOverMobile()
      PlayState.PREPARED,
      PlayState.BUFFING,
      PlayState.BUFFED,
      PlayState.SEEKING,
      PlayState.SEEKED,
      PlayState.START,
      PlayState.PAUSE,
      PlayState.STOP -> setOverNormal()
      PlayState.COMPLETE -> setOverComplete()
      else -> {
      }
    }
  }

  override fun callPlayProgress(progress: Long) {
    mPlayProgress = progress
    callController?.callPlayProgress(progress)
  }

  override fun callBufferProgress(progress: Long) {
    callController?.callBufferProgress(progress)
  }

  override fun callBufferPercent(percent: Int, kbps: Float) {
    callLoading?.callBufferPercent(percent, kbps)
  }

  override fun callSnapShot(bm: Bitmap, width: Int, height: Int) {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="根据状态显示和隐藏控制器">
  private fun setOverNormal() {
    getAllChildView(true).forEach { it.visibleGone(it !is VideoErrorCallListener && it !is VideoCompleteCallListener) }
  }

  private fun setOverError() {
    getAllChildView(true).forEach { it.visibleGone(it is VideoErrorCallListener) }
  }

  private fun setOverComplete() {
    getAllChildView(true).forEach { it.visibleGone(it is VideoCompleteCallListener) }
  }

  private fun setOverCover() {
    getAllChildView(true).forEach { it.visibleGone(it is VideoCoverCallListener) }
  }

  private fun setOverMobile() {
    getAllChildView(true).forEach { it.visibleGone(it is VideoCoverCallListener || it is VideoErrorCallListener) }
  }
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

  //播放结束的回调
  private var callComplete: VideoCompleteCallListener? = null

  //封面显示的回调
  private var callCover: VideoCoverCallListener? = null
  //</editor-fold>
}