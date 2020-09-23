package com.cc.video.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.cc.ext.*
import com.cc.video.R
import com.cc.video.inter.call.VideoCoverCallListener
import com.cc.video.inter.operate.VideoCoverListener
import kotlinx.android.synthetic.main.layout_video_cover.view.cover_bg
import kotlinx.android.synthetic.main.layout_video_cover.view.cover_btn

/**
 * 视频封面展示View
 * Author:CASE
 * Date:2020-9-23
 * Time:10:07
 */
abstract class VideoCoverView @JvmOverloads constructor(
    con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), VideoCoverCallListener {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //操作
  private var coverListener: VideoCoverListener? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_video_cover, this, true)
    //按压效果
    cover_btn.pressEffectAlpha(0.9f)
    //点击事件
    cover_btn.click {
      coverListener?.startPlay()
      callHiddenView()
    }
    //防止触发后面的事件
    cover_bg.click { }
    this.callHiddenView()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="播放器回调">
  override fun callCoverUrl(url: String?) {
    url?.let { loadVideoCover(it, cover_bg) }
  }

  override fun callShowAllView() {
    cover_bg.visible()
    cover_btn.visible()
  }

  override fun callShowPlayView() {
    cover_btn.visible()
  }

  override fun callHiddenView() {
    cover_bg.gone()
    cover_btn.gone()
  }

  override fun setCall(call: VideoCoverListener?) {
    coverListener = call
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="需要外部重新封面加载">
  abstract fun loadVideoCover(url: String, iv: ImageView)
  //</editor-fold>
}