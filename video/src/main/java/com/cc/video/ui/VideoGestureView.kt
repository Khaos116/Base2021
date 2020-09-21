package com.cc.video.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.ext.logE
import com.cc.video.inter.*
import com.cc.video.utils.VideoTimeUtils
import kotlinx.android.synthetic.main.layout_video_gesture.view.*
import kotlin.math.*

/**
 * Author:CASE
 * Date:2020-9-19
 * Time:18:22
 */
@SuppressLint("SetTextI18n")
class VideoGestureView @JvmOverloads constructor(
  con: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoGestureCallListener, OverGestureListener {

  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  private var gestureListener: VideoGestureListener? = null

  //是否可以滑动屏幕操作
  private var canGestureVideo: Boolean = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_gesture, this, true)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势按下显示操作">
  private fun showBright() {
    gestureListener?.let { gl ->
      val cur = gl.getBrightCurrent()
      val min = gl.getBrightMin()
      downBright = cur
      gesture_bright_view.visible()
      gesture_bright_iv.setImageResource(if (cur == min) R.drawable.svg_media_bright_sub else R.drawable.svg_media_bright_add)
      gesture_bright_tv.text = String.format("%d%%", (cur * 100).toInt())
    }
  }

  private fun showVolume() {
    gestureListener?.let { gl ->
      val cur = gl.getVolumeCurrent()
      val min = gl.getVolumeMin()
      downVolume = cur
      gesture_volume_view.visible()
      gesture_volume_iv.setImageResource(if (cur == min) R.drawable.svg_media_mute else R.drawable.svg_media_volume_add)
      gesture_volume_tv.text = String.format("%d%%", (cur * 100).toInt())
    }
  }

  private fun showProgress() {
    gestureListener?.let { gl ->
      gl.onPause()
      val cur = gl.getCurrentPosition()
      val max = gl.getDuration()
      downSeconds = cur / 1000
      gesture_seek_view.visible()
      gesture_seek_tv1.text = "+0s"
      gesture_seek_tv2.text = String.format(
        "%s/%s",
        VideoTimeUtils.instance.forMatterVideoTime(cur), VideoTimeUtils.instance.forMatterVideoTime(max)
      )
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势滑动修改UI">
  private var downSeconds: Long = 0
  private var downBright: Float = 0f
  private var downVolume: Float = 0f
  private var lastBright: Float = 0f
  private var lastVolume: Float = 0f

  private fun setBright(bright: Float) {
    if (bright > lastBright) gesture_bright_iv.setImageResource(R.drawable.svg_media_bright_add)
    else if (bright < lastBright) gesture_bright_iv.setImageResource(R.drawable.svg_media_bright_sub)
    gesture_bright_tv.text = String.format("%d%%", (bright * 100).toInt())
    lastBright = bright
    gestureListener?.setBright(bright)
  }

  private fun setVolume(volume: Float) {
    when {
      volume == gestureListener?.getVolumeMin() ?: 0f -> gesture_volume_iv.setImageResource(R.drawable.svg_media_mute)
      volume > lastVolume -> gesture_volume_iv.setImageResource(R.drawable.svg_media_volume_add)
      volume < lastVolume -> gesture_volume_iv.setImageResource(R.drawable.svg_media_volume_sub)
    }
    gesture_volume_tv.text = String.format("%d%%", (volume * 100).toInt())
    lastVolume = volume
    gestureListener?.setVolume(volume)
  }

  private fun setSeekToPreView(msc: Long) {
    val currentSeconds = msc / 1000
    gesture_seek_tv1.text = String.format(
      "%s%s", if (currentSeconds >= downSeconds) "+" else "-",
      VideoTimeUtils.instance.forMatterVideoTimeSeek(abs(currentSeconds - downSeconds))
    )
    gesture_seek_tv2.text = String.format(
      "%s/%s", VideoTimeUtils.instance.forMatterVideoTime(currentSeconds * 1000),
      VideoTimeUtils.instance.forMatterVideoTime(gestureListener?.getDuration() ?: 0)
    )
    gestureListener?.seekPreviewTo(msc)
  }

  private fun setSeekToReally(msc: Long) {
    if (msc / 1000 != downSeconds) gestureListener?.seekTo(msc)
    gestureListener?.onStart()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势滑动计算">
  private var newPositionVideo: Long = 0
  private fun onHorizontalSlide(percent: Float) {
    gestureListener?.let { gl ->
      if (gesture_seek_view.visibility != View.VISIBLE) showProgress()
      //最大进度不能超过总时长的一半
      val duration = gl.getDuration()
      val maxChange = min(duration / 2, duration - downSeconds * 1000)
      newPositionVideo = max(0, min(duration, downSeconds * 1000 + (maxChange * percent).toLong()))
      setSeekToPreView(newPositionVideo)
    }
  }

  private fun onBrightVerticalSlide(percent: Float) {
    gestureListener?.let { gl ->
      if (gesture_bright_view.visibility != View.VISIBLE) showBright()
      setBright(max(gl.getBrightMin(), min(1f, downBright + gl.getBrightMax() * percent)))
    }
  }

  private fun onVolumeVerticalSlide(percent: Float) {
    gestureListener?.let { gl ->
      if (gesture_volume_view.visibility != View.VISIBLE) showVolume()
      setVolume(max(gl.getVolumeMin(), min(1f, downVolume + gl.getVolumeMax() * percent)))
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调">
  override fun setCall(call: VideoGestureListener) {
    gestureListener = call
  }

  override fun callOperate(canOperate: Boolean) {
    canGestureVideo = canOperate
    if (!canOperate) {
      //请求父控件拦截
      this.requestDisallowInterceptTouchEvent(false)
      gesture_bright_view.gone()
      gesture_volume_view.gone()
      gesture_seek_view.gone()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势回调">
  override fun callOverClick() {}

  override fun callOverDoubleClick() {}

  private var firstTouch = false
  private var horizontalSlide = false
  private var rightVerticalSlide = false
  override fun callOverScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {
    if (e1 != null && e2 != null && canGestureVideo && gestureListener != null) {
      val mOldX = e1.x
      val mOldY = e1.y
      val deltaY = mOldY - e2.y
      val deltaX = mOldX - e2.x
      if (firstTouch) {
        horizontalSlide = abs(distanceX) >= abs(distanceY)
        rightVerticalSlide = mOldX > width * 0.5f
        firstTouch = false
      }
      if (horizontalSlide) onHorizontalSlide(-deltaX / width)
      else {
        if (abs(deltaY) > height) return
        if (rightVerticalSlide) onVolumeVerticalSlide(deltaY / height)
        else onBrightVerticalSlide(deltaY / height)
      }
    }
  }

  override fun callOverTouchDown(e: MotionEvent?) {
    firstTouch = true
  }

  override fun callOverTouchUp() {
    if (gesture_seek_view.visibility == View.VISIBLE && canGestureVideo) setSeekToReally(newPositionVideo)
    firstTouch = false
    gesture_bright_view.gone()
    gesture_volume_view.gone()
    gesture_seek_view.gone()
  }
  //</editor-fold>
}