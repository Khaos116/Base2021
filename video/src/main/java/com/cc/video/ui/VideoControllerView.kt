package com.cc.video.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.click
import com.cc.ext.pressEffectAlpha
import com.cc.video.R
import com.cc.video.inter.*
import com.cc.video.utils.VideoTimeUtils
import kotlinx.android.synthetic.main.layout_video_controller.view.*
import kotlinx.coroutines.*

/**
 * Author:CASE
 * Date:2020-9-18
 * Time:16:34
 */
@SuppressLint("SetTextI18n")
class VideoControllerView @JvmOverloads constructor(
  private val con: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoControllerCallListener, OverGestureListener {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  private var controllerListener: VideoControllerListener? = null

  //是否可以进行操作
  private var canOperateVideo: Boolean = false

  //倒计时隐藏
  private var job: Job? = null
  private var jobLock: Job? = null

  //是否正在拖动
  private var isSeeking = false

  //是否加锁
  private var isLocked = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_controller, this, true)
    //默认值
    controller_bottom_duration.text = "00:00:00"
    controller_bottom_time.text = "00:00:00"
    controller_bottom_seekbar.max = 0
    controller_bottom_progressbar.max = 0
    //按压
    controller_top_back.pressEffectAlpha(0.95f)
    controller_bottom_play_pause.pressEffectAlpha(0.95f)
    controller_bottom_stop.pressEffectAlpha(0.95f)
    controller_bottom_full_screen.pressEffectAlpha(0.95f)
    controller_lock_state.pressEffectAlpha(0.95f)
    //点击事件
    controller_top_back.click {
      controllerListener?.onBack()
      countDownHidden()
      countDownHiddenLock()
    }
    controller_bottom_play_pause.click {
      if (!canOperateVideo) return@click
      controllerListener?.onPlayOrPause()
      countDownHidden()
      countDownHiddenLock()
    }
    controller_bottom_full_screen.click {
      controllerListener?.fullScreenOrExit()
      countDownHidden()
      countDownHiddenLock()
    }
    controller_bottom_stop.click {
      controllerListener?.onStop()
      job?.cancel()
    }
    controller_lock_state.click {
      if (controller_lock_state.isSelected) {
        unLock()
      } else {
        lock()
      }
    }
    controller_bottom_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
          controller_bottom_seekbar.progress = progress
          controller_bottom_time.text = VideoTimeUtils.instance.forMatterVideoTime(progress * 1000L)
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {
        isSeeking = true
        controllerListener?.onPause()
        job?.cancel()
      }

      override fun onStopTrackingTouch(seekBar: SeekBar) {
        controllerListener?.seekTo(seekBar.progress * 1000L)
        controllerListener?.onPlay()
        isSeeking = false
        countDownHidden()
        countDownHiddenLock()
      }
    })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="隐藏和显示">
  //展示播放状态
  private fun showView() {
    controller_bottom_progressbar?.animate()?.alpha(0f)?.start()
    controller_top_container?.animate()?.alpha(1f)?.start()
    controller_bottom_container?.animate()?.alpha(1f)?.start()
  }

  //隐藏播放状态
  private fun hiddenView() {
    controller_bottom_progressbar?.animate()?.alpha(1f)?.start()
    controller_top_container?.animate()?.alpha(0f)?.start()
    controller_bottom_container?.animate()?.alpha(0f)?.start()
  }

  //隐藏锁图标
  private fun showLock() {
    controller_lock_state?.animate()?.alpha(1f)?.start()
  }

  //显示锁图标
  private fun hiddenLock() {
    controller_lock_state?.animate()?.alpha(0f)?.start()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调">
  override fun callPrepare() {
    callPause()
  }

  override fun callPlay() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = false
    if (controller_bottom_progressbar.alpha != 1f) {
      countDownHidden()
      countDownHiddenLock()
    }
  }

  override fun callPause() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = true
    job?.cancel()
    unLock()
    showView()
    showLock()
  }

  override fun callStop() {
    callPause()
    controller_bottom_progressbar.progress = 0
    controller_bottom_seekbar.progress = 0
    controller_bottom_time.text = "00:00:00"
    job?.cancel()
    jobLock?.cancel()
    unLock()
    showView()
    showLock()
  }

  override fun callComplete() {
    callStop()
  }

  private fun lock() {
    isLocked = true
    controller_lock_state.isSelected = true
    job?.cancel()
    jobLock?.cancel()
    hiddenView()
    countDownHiddenLock()
    controllerListener?.lock()
  }

  override fun unLock() {
    isLocked = false
    controller_lock_state.isSelected = false
    job?.cancel()
    jobLock?.cancel()
    showView()
    showLock()
    if (!controller_bottom_play_pause.isSelected) countDownHidden()
    controllerListener?.unlock()
  }

  override fun callTitle(title: String) {
    controller_top_title.text = title
  }

  override fun callBufferProgress(msc: Long) {
    controller_bottom_progressbar.secondaryProgress = msc.toInt()
    controller_bottom_seekbar.secondaryProgress = msc.toInt() / 1000
  }

  override fun callProgress(msc: Long) {
    if (!isSeeking) {
      controller_bottom_progressbar.progress = msc.toInt()
      controller_bottom_seekbar.progress = msc.toInt() / 1000
      controller_bottom_time.text = VideoTimeUtils.instance.forMatterVideoTime(msc)
    }
  }

  override fun callDuration(duration: Long) {
    if (controller_bottom_progressbar.max == 0) {
      controller_bottom_progressbar.alpha = 1f
      controller_top_container.alpha = 0f
      controller_bottom_container.alpha = 0f
    }
    controller_bottom_progressbar.max = duration.toInt()
    controller_bottom_seekbar.max = duration.toInt() / 1000
    controller_bottom_duration.text = VideoTimeUtils.instance.forMatterVideoTime(duration)

  }

  override fun enterFullScreen() {
    controller_bottom_full_screen.isSelected = true
  }

  override fun exitFullScreen() {
    controller_bottom_full_screen.isSelected = false
  }

  override fun callOperate(canOperate: Boolean) {
    this.canOperateVideo = canOperate
    controller_bottom_seekbar.isEnabled = canOperate
    if (canOperate) {
      showView()
      showLock()
      if (!controller_bottom_play_pause.isSelected) {
        countDownHidden()
        countDownHiddenLock()
      }
    } else {
      hiddenView()
      hiddenLock()
    }
  }

  override fun setCall(call: VideoControllerListener?) {
    controllerListener = call
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势回调">
  override fun callOverClick() {
    if (isLocked) { //加锁的情况下
      if (controller_lock_state.alpha == 1f) {
        hiddenLock()
      } else if (controller_lock_state.alpha == 0f) {
        showLock()
      }
    } else { //没有加锁的情况
      if (controller_bottom_progressbar.alpha == 1f) { //没有显示控制器
        showView()
        showLock()
        if (!controller_bottom_play_pause.isSelected) {
          countDownHidden()
          countDownHiddenLock()
        }
      } else if (!controller_bottom_play_pause.isSelected) { //显示了控制器，没有处于暂停状态
        hiddenView()
        hiddenLock()
      }
    }
  }

  override fun callOverDoubleClick() {
    if (isLocked) return
    controllerListener?.onPlayOrPause()
  }

  override fun callOverScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float) {}

  override fun callOverTouchDown(e: MotionEvent?) {}

  override fun callOverTouchUp() {}
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="倒计时隐藏">
  private fun countDownHidden() {
    job?.cancel()
    job = GlobalScope.launch(Dispatchers.Main) {
      delay(5 * 1000)
      if (isActive && controller_bottom_progressbar?.alpha ?: 1f != 1f) {
        hiddenView()
      }
    }
  }

  private fun countDownHiddenLock() {
    jobLock?.cancel()
    jobLock = GlobalScope.launch(Dispatchers.Main) {
      delay(5 * 1000)
      if (isActive && controller_lock_state?.alpha ?: 1f != 1f) {
        hiddenLock()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="移除">
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    job?.cancel()
    jobLock?.cancel()
  }
  //</editor-fold>
}