package com.cc.video.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.inter.*
import kotlinx.android.synthetic.main.layout_video_complete.view.complete_view

/**
 * 视频播放结束后的回调
 * Author:CASE
 * Date:2020-9-22
 * Time:18:44
 */
class VideoCompleteView @JvmOverloads constructor(
  private val con: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoCompleteCallListener {

  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作监听
  private var completeListener: VideoCompleteListener? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_complete, this, true)
    complete_view.pressEffectAlpha(0.95f)
    complete_view.click {
      completeListener?.resetPlay()
      complete_view.gone()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放结束的回调">
  override fun callComplete() {
    complete_view?.visible()
  }

  override fun setCall(call: VideoCompleteListener?) {
    completeListener = call
  }
  //</editor-fold>
}