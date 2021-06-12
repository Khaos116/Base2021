package com.cc.video.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.enu.PlayState
import com.cc.video.inter.call.VideoCompleteCallListener
import com.cc.video.inter.operate.VideoCompleteListener
import kotlinx.android.synthetic.main.layout_video_complete.view.*

/**
 * 视频播放结束后的回调
 * Author:Khaos
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

  //<editor-fold defaultstate="collapsed" desc="外部设置">
  //设置是否是直播(直播没有时长，播放进度等)
  fun setLiveVideo(live: Boolean) {

  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_complete, this, true)
    complete_iv.pressEffectAlpha(0.9f)
    complete_tv.pressEffectAlpha(0.9f)
    complete_iv.click {
      completeListener?.resetPlay()
      complete_view.gone()
    }
    complete_tv.click { complete_iv.performClick() }
    //防止透过去点击
    complete_view.click {}
  }
  //</editor-fold>

  override fun callPlayState(state: PlayState) {
    if (state == PlayState.COMPLETE) complete_view?.visible()
  }

  override fun setCall(call: VideoCompleteListener?) {
    completeListener = call
  }
  //</editor-fold>
}