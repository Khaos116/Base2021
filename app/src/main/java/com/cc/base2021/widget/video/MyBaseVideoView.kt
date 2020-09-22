package com.cc.base2021.widget.video

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.lifecycle.*
import com.cc.ext.logE
import com.kk.taurus.playerbase.widget.BaseVideoView

/**
 * Author:CASE
 * Date:2020-9-17
 * Time:15:37
 */
class MyBaseVideoView @JvmOverloads constructor(
  private val mContext: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : BaseVideoView(mContext, attrs, defStyleAttr), LifecycleObserver {

  //<editor-fold defaultstate="collapsed" desc="初始化">
  init {
    setBackgroundColor(Color.BLACK)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Lifecycle生命周期">
  private var mLifecycle: Lifecycle? = null

  //通过Lifecycle内部自动管理暂停和播放(如果不需要后台播放)
  fun setLifecycleOwner(owner: LifecycleOwner?) {
    if (owner == null) {
      mLifecycle?.removeObserver(this)
      mLifecycle = null
    } else {
      mLifecycle?.removeObserver(this)
      mLifecycle = owner.lifecycle
      mLifecycle?.addObserver(this)
    }
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
  private fun onResumeVideo() {
    "onResumeVideo".logE()
    resume()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
  private fun onPauseVideo() {
    "onPauseVideo".logE()
    pause()
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
  private fun onDestroyVideo() {
    "onDestroyVideo".logE()
    stopPlayback()
  }
  //</editor-fold>
}