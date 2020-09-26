package com.cc.video.ui

import android.annotation.SuppressLint
import android.content.*
import android.os.BatteryManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.TimeUtils
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState
import com.cc.video.inter.*
import com.cc.video.inter.call.VideoControllerCallListener
import com.cc.video.inter.operate.VideoControllerListener
import com.cc.video.utils.VideoTimeUtils
import kotlinx.android.synthetic.main.layout_video_controller.view.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 播放器常用控制器,主要实现：
 * 1.播放器的暂停、播放、停止、全屏、进度拖动、播放加锁
 * 2.页面返回按钮(触发Activity的onBackPressed)、标题显示、视频时长和进度显示、电池电量、系统时间显示等
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
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoControllerCallListener,
    OverGestureListener {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  private var controllerListener: VideoControllerListener? = null

  //电池监听
  private var hasRegisterBattery: Boolean = false
  private var batteryTimeReceiver: BatteryAndTimeReceiver? = null

  //时间显示
  private var timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

  //是否可以进行操作
  private var canOperateVideo: Boolean = false

  //倒计时隐藏
  private var job: Job? = null
  private var jobLock: Job? = null

  //是否正在拖动
  private var isSeeking = false

  //是否加锁
  private var isLocked = false

  //当前播放状态
  private var mPlayState: PlayState = PlayState.SET_DATA
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部设置">
  //设置是否是直播(直播没有时长，播放进度等)
  fun setLiveVideo(live: Boolean) {

  }
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
    controller_top_back.pressEffectAlpha(0.9f)
    controller_bottom_play_pause.pressEffectAlpha(0.9f)
    controller_bottom_stop.pressEffectAlpha(0.9f)
    controller_bottom_full_screen.pressEffectAlpha(0.9f)
    controller_lock_state.pressEffectAlpha(0.9f)
    //点击事件
    controller_top_back.click {
      if (controller_top_container.alpha != 1f) return@click
      controllerListener?.onBack()
      countDownHidden()
      countDownHiddenLock()
    }
    controller_bottom_play_pause.click {
      if (controller_bottom_container.alpha != 1f) return@click
      if (mPlayState == PlayState.PAUSE || mPlayState == PlayState.START) {
        controllerListener?.onPlayOrPause()
        countDownHidden()
        countDownHiddenLock()
      } else if (mPlayState == PlayState.STOP || mPlayState == PlayState.COMPLETE || mPlayState == PlayState.ERROR) {
        controllerListener?.onPlay()
        countDownHidden()
        countDownHiddenLock()
      }
    }
    controller_bottom_full_screen.click {
      if (controller_bottom_container.alpha != 1f) return@click
      controllerListener?.fullScreenOrExit()
      countDownHidden()
      countDownHiddenLock()
    }
    controller_bottom_stop.click {
      if (controller_bottom_container.alpha != 1f) return@click
      controllerListener?.onStop()
      job?.cancel()
      jobLock?.cancel()
    }
    controller_lock_state.click {
      if (!canOperateVideo) return@click
      if (controller_lock_state.isSelected) {
        unLock()
        showControllerView()
        if (!controller_bottom_play_pause.isSelected) {
          countDownHidden()
          countDownHiddenLock()
        }
      } else {
        lock()
        hiddenControllerView()
        if (!controller_bottom_play_pause.isSelected) countDownHiddenLock()
      }
    }
    controller_bottom_seekbar.setOnSeekBarChangeListener(object :
        SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
          controller_bottom_seekbar.progress = progress
          controller_bottom_time.text =
              VideoTimeUtils.instance.forMatterVideoTime(progress * 1000L)
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {
        isSeeking = true
        job?.cancel()
        jobLock?.cancel()
      }

      override fun onStopTrackingTouch(seekBar: SeekBar) {
        controllerListener?.seekTo(seekBar.progress * 1000L)
        controllerListener?.onPlay()
        isSeeking = false
        countDownHidden()
        countDownHiddenLock()
      }
    })
    //电池监听
    batteryTimeReceiver = BatteryAndTimeReceiver(
        controller_top_battery_iv,
        controller_top_battery_tv,
        controller_top_sys_time,
        timeFormat
    )
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="隐藏和显示">
  //展示播放状态
  private fun showControllerView() {
    job?.cancel()
    controller_top_sys_time?.text = TimeUtils.millis2String(System.currentTimeMillis(), timeFormat)
    controller_bottom_progressbar?.animate()?.alpha(0f)?.start()
    controller_top_container?.animate()?.alpha(1f)?.start()
    controller_bottom_container?.animate()?.alpha(1f)?.start()
    controller_top_battery_iv?.animate()?.alpha(1f)?.start()
    controller_top_battery_tv?.animate()?.alpha(1f)?.start()
    controller_top_sys_time?.animate()?.alpha(1f)?.start()
    controller_bottom_seekbar?.isEnabled = true
  }

  //隐藏播放状态
  private fun hiddenControllerView() {
    job?.cancel()
    controller_bottom_progressbar?.animate()?.alpha(1f)?.start()
    controller_top_container?.animate()?.alpha(0f)?.start()
    controller_bottom_container?.animate()?.alpha(0f)?.start()
    controller_top_battery_iv?.animate()?.alpha(0f)?.start()
    controller_top_battery_tv?.animate()?.alpha(0f)?.start()
    controller_top_sys_time?.animate()?.alpha(0f)?.start()
    controller_bottom_seekbar?.isEnabled = false
  }

  //隐藏锁图标
  private fun showLock() {
    jobLock?.cancel()
    controller_lock_state?.animate()?.alpha(1f)?.start()
  }

  //显示锁图标
  private fun hiddenLock() {
    jobLock?.cancel()
    controller_lock_state?.animate()?.alpha(0f)?.start()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调">
  override fun callPlayState(state: PlayState) {
    mPlayState = state
    checkOperate(state)
    when (state) {
      PlayState.START -> showStartView()
      PlayState.PAUSE -> showPauseView()
      PlayState.STOP, PlayState.COMPLETE, PlayState.ERROR -> showOriginView()
      else -> {
      }
    }
  }

  override fun callUiState(uiState: PlayUiState) {
    when (uiState) {
      PlayUiState.EXIT_FULL -> exitFullScreen()
      PlayUiState.ENTER_FULL -> enterFullScreen()
      else -> {
      }
    }
  }

  override fun callVideoInfo(url: String, title: String?, cover: String?) {
    controller_top_title.text = title
    controller_bottom_progressbar.alpha = 1f
    controller_top_container.alpha = 0f
    controller_bottom_container.alpha = 0f
  }

  override fun callDuration(duration: Long) {
    controller_bottom_progressbar.max = duration.toInt()
    controller_bottom_seekbar.max = duration.toInt() / 1000
    controller_bottom_duration.text = VideoTimeUtils.instance.forMatterVideoTime(duration)
  }

  override fun callPlayProgress(progress: Long) {
    if (!isSeeking) {
      controller_bottom_progressbar.progress = progress.toInt()
      controller_bottom_seekbar.progress = progress.toInt() / 1000
      controller_bottom_time.text = VideoTimeUtils.instance.forMatterVideoTime(progress)
    }
  }

  override fun callBufferProgress(progress: Long) {
    controller_bottom_progressbar.secondaryProgress = progress.toInt()
    controller_bottom_seekbar.secondaryProgress = progress.toInt() / 1000
  }

  override fun setCall(call: VideoControllerListener?) {
    controllerListener = call
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调设置UI">
  //检查是否可以进行操作
  private fun checkOperate(state: PlayState) {
    canOperateVideo = !(state == PlayState.SET_DATA || state == PlayState.SHOW_MOBILE || state == PlayState.PREPARING ||
        state == PlayState.BUFFING || state == PlayState.SEEKING || state == PlayState.STOP ||
        state == PlayState.COMPLETE || state == PlayState.ERROR)
    controller_bottom_seekbar.isEnabled = canOperateVideo
  }

  //显示默认View
  private fun showOriginView() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = true
    callPlayProgress(0)
    callBufferProgress(0)
    showControllerView()
    isLocked = false
    controller_lock_state.isSelected = false
    controllerListener?.unlock()
    hiddenLock()
  }

  //展示播放中的状态
  private fun showStartView() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = false
    if (controller_bottom_container.alpha == 1f) {
      showLock()
      countDownHidden()
      countDownHiddenLock()
    }
  }

  //展示暂停状态
  private fun showPauseView() {
    controller_bottom_play_pause.setImageResource(R.drawable.selector_play_state)
    controller_bottom_play_pause.isSelected = true
    unLock()
    showControllerView()
  }

  //进入全屏
  private fun enterFullScreen() {
    controller_bottom_full_screen.isSelected = true
    controller_top_battery_iv.visible()
    controller_top_battery_tv.visible()
    controller_top_sys_time.visible()
  }

  //退出全屏幕
  private fun exitFullScreen() {
    controller_bottom_full_screen.isSelected = false
    controller_top_battery_iv.gone()
    controller_top_battery_tv.gone()
    controller_top_sys_time.gone()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部加锁和解锁的操作">
  private fun lock() {
    isLocked = true
    controller_lock_state.isSelected = true
    jobLock?.cancel()
    countDownHiddenLock()
    controllerListener?.lock()
  }

  private fun unLock() {
    isLocked = false
    controller_lock_state.isSelected = false
    jobLock?.cancel()
    showLock()
    controllerListener?.unlock()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="手势回调">
  override fun callOverClick() {
    if (!canOperateVideo) return
    if (isLocked) { //加锁的情况下
      if (controller_lock_state.alpha == 1f) {
        hiddenLock()
      } else if (controller_lock_state.alpha == 0f) {
        showLock()
        countDownHiddenLock()
      }
    } else { //没有加锁的情况
      if (controller_bottom_container.alpha == 0f) { //没有显示控制器
        showControllerView()
        showLock()
        if (!controller_bottom_play_pause.isSelected) {
          countDownHidden()
          countDownHiddenLock()
        }
      } else if (!controller_bottom_play_pause.isSelected) { //显示了控制器，没有处于暂停状态
        hiddenControllerView()
        hiddenLock()
      }
    }
  }

  override fun callOverDoubleClick() {
    if (!isLocked && canOperateVideo) controllerListener?.onPlayOrPause()
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
      if (isActive && controller_bottom_container?.alpha ?: 0f != 0f) {
        hiddenControllerView()
      }
    }
  }

  private fun countDownHiddenLock() {
    jobLock?.cancel()
    jobLock = GlobalScope.launch(Dispatchers.Main) {
      delay(5 * 1000)
      if (isActive && controller_lock_state?.alpha ?: 0f != 0f) {
        hiddenLock()
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加和移除">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    if (!hasRegisterBattery) batteryTimeReceiver?.let {
      hasRegisterBattery = true
      con.registerReceiver(it, IntentFilter(Intent.ACTION_BATTERY_CHANGED).apply { //电量变化的监听
        addAction(Intent.ACTION_POWER_CONNECTED) //充电连接的监听
        addAction(Intent.ACTION_POWER_DISCONNECTED) //充电断开的监听
        addAction(Intent.ACTION_TIME_TICK) //系统每分钟会发出该广播
      })
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    job?.cancel()
    jobLock?.cancel()
    if (hasRegisterBattery) {
      hasRegisterBattery = false
      batteryTimeReceiver?.let { con.unregisterReceiver(it) }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="电池和系统时间的监听">
  class BatteryAndTimeReceiver(
      private val ivBattery: ImageView,
      private val tvBattery: TextView,
      private val tvSysTime: TextView,
      private val timeFormatter: SimpleDateFormat
  ) : BroadcastReceiver() {
    //充电状态
    private var chargeState: Boolean? = null

    //防止刷新太快
    private var lastBatteryTime: Long = 0

    //https://developer.android.google.cn/training/monitoring-device-state/battery-monitoring.html?hl=zh-cn
    override fun onReceive(context: Context?, intent: Intent?) {
      if (intent?.action == Intent.ACTION_TIME_TICK) {
        tvSysTime.text = TimeUtils.millis2String(System.currentTimeMillis(), timeFormatter)
        "更新系统时间:${tvSysTime.text}".logI()
        return
      }
      intent?.extras?.let {
        //充电状态
        val status = it.getInt(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL)
        if (isCharging != chargeState) {
          chargeState = isCharging
          if (isCharging) {
            val chargePlug = it.getInt(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
            ivBattery.drawable?.level = 0
            "手机正在充电,充电方式:${if (usbCharge) "USB充电" else if (acCharge) "交流电充电" else "未知"}".logI()
          } else "手机断开充电".logI()
        }
        if (System.currentTimeMillis() - lastBatteryTime < 10 * 1000) return //最小10秒刷新一次电量
        lastBatteryTime = System.currentTimeMillis()
        val current = it.getInt(BatteryManager.EXTRA_LEVEL, -1) //获得当前电量
        val total = it.getInt(BatteryManager.EXTRA_SCALE, -1) //获得总电量
        val percent = (current * 100f / total).toInt()
        ivBattery.drawable?.level = if (isCharging) 0 else maxOf(1, percent)
        tvBattery.text = String.format("%s%%", maxOf(1, percent))
        "当前手机电量:${percent}%".logI()
      }
    }
  }
  //</editor-fold>
}