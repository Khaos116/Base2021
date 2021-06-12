package com.cc.video.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.enu.PlayState
import com.cc.video.inter.call.VideoErrorCallListener
import com.cc.video.inter.operate.VideoErrorListener
import kotlinx.android.synthetic.main.layout_video_error.view.*
import kotlinx.android.synthetic.main.layout_video_error.view.error_retry

/**
 * 播放器异常回调显示，可以操作重试和移动网络的继续播放
 * Author:Khaos
 * Date:2020-9-19
 * Time:14:38
 */
@SuppressLint("SetTextI18n")
class VideoErrorView @JvmOverloads constructor(
    private val con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoErrorCallListener {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  private var errorListener: VideoErrorListener? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部设置">
  //设置是否是直播(直播没有时长，播放进度等)
  fun setLiveVideo(live: Boolean) {

  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_error, this, true)
    //按压效果
    error_retry.pressEffectAlpha(0.9f)
    //点击事件
    error_retry.click {
      showError(false)
      if (error_retry.text.toString() == StringUtils.getString(R.string.video_continue)) {
        errorListener?.continuePlay()
      } else {
        errorListener?.resetPlay()
      }
    }
    //防止透过去点到其他
    error_view.click { }
    //默认不显示
    showError(false)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="显示和隐藏">
  private fun showError(show: Boolean) {
    error_view?.visibleGone(show)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调">
  @SuppressLint("MissingPermission")
  override fun callPlayState(state: PlayState) {
    if (state == PlayState.SHOW_MOBILE) {
      error_info.text = StringUtils.getString(R.string.video_error_mobile)
      error_retry.text = StringUtils.getString(R.string.video_continue)
      showError(true)
    } else if (state == PlayState.ERROR) {
      if (ContextCompat.checkSelfPermission(con, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
          && !NetworkUtils.isConnected()) {
        error_info.text = StringUtils.getString(R.string.video_error_no_net)
        error_retry.text = StringUtils.getString(R.string.video_retry)
        showError(true)
      } else {
        error_info.text = StringUtils.getString(R.string.video_error_default)
        error_retry.text = StringUtils.getString(R.string.video_retry)
        showError(true)
      }
    }
  }

  override fun setCall(call: VideoErrorListener) {
    errorListener = call
  }
  //</editor-fold>
}