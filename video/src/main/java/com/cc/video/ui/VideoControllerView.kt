package com.cc.video.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.inter.VideoControllerCallListener
import com.cc.video.inter.VideoControllerListener
import kotlinx.android.synthetic.main.layout_controller.view.*
import kotlinx.coroutines.*
import java.util.Formatter
import java.util.Locale

/**
 * Author:CASE
 * Date:2020-9-18
 * Time:16:34
 */
@SuppressLint("SetTextI18n")
class VideoControllerView @JvmOverloads constructor(
  private val mContext: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(mContext, attrs, defStyleAttr, defStyleRes), VideoControllerCallListener {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  var controllerListener: VideoControllerListener? = null

  //倒计时隐藏
  private var job: Job? = null

  //是否正在拖动
  private var isSeeking = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    LayoutInflater.from(mContext).inflate(R.layout.layout_controller, this, true)
    //默认值
    controller_bottom_duration.text = "00:00:00"
    controller_bottom_time.text = "00:00:00"
    controller_bottom_seekbar.max = 0
    controller_bottom_progressbar.max = 0
    //按压
    controller_top_back.pressEffectAlpha(0.95f)
    controller_bottom_play_pause.pressEffectAlpha(0.95f)
    controller_bottom_full_screen.pressEffectAlpha(0.95f)
    //点击事件
    controller_top_back.click {
      controllerListener?.onBack()
      countDownHidden()
    }
    controller_bottom_play_pause.click {
      controllerListener?.onPlayOrPause()
      countDownHidden()
    }
    controller_bottom_full_screen.click {
      controllerListener?.fullScreenOrExit()
      countDownHidden()
    }
    controller_bottom_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
          controller_bottom_seekbar.progress = progress
          controller_bottom_time.text = forMateVideoTime(progress * 1000L)
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
      }
    })
    this.click {
      if (controller_bottom_progressbar.alpha == 1f) {
        showView()
        if (!controller_bottom_play_pause.isSelected) countDownHidden()
      } else if (!controller_bottom_play_pause.isSelected) {
        hiddenView()
      }
    }
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="回调处理">
  override fun callPlay() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = false
    if (controller_bottom_progressbar.alpha != 1f) countDownHidden()
  }

  override fun callPause() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = true
    job?.cancel()
    showView()
  }

  override fun callStop() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    job?.cancel()
    showView()
  }

  override fun callComplete() {
    callPause()
    controller_bottom_progressbar.progress = 0
    controller_bottom_seekbar.progress = 0
    controller_bottom_time.text = "00:00:00"
    job?.cancel()
    showView()
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
      controller_bottom_time.text = forMateVideoTime(msc)
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
    controller_bottom_duration.text = forMateVideoTime(duration)

  }

  override fun enterFullScreen() {
    controller_bottom_full_screen.isSelected = true
  }

  override fun exitFullScreen() {
    controller_bottom_full_screen.isSelected = false
  }

  override fun setController(call: VideoControllerListener?) {
    controllerListener = call
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="时间处理">
  private var formatterBuilder: StringBuilder = StringBuilder()
  private var formatterHHMM: Formatter = Formatter(formatterBuilder, Locale.getDefault())
  private fun forMateVideoTime(time: Long): String {
    val totalSeconds: Int = (time / 1000).toInt()
    val seconds: Int = totalSeconds % 60
    val minutes: Int = totalSeconds / 60 % 60
    val hours: Int = totalSeconds / 3600
    formatterBuilder.setLength(0)
    return if (hours > 0) {
      formatterHHMM.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
      formatterHHMM.format("00:%02d:%02d", minutes, seconds).toString()
    }
  }
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
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="移除">
  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    job?.cancel()
  }
  //</editor-fold>
}