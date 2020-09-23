package com.cc.video.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import com.cc.ext.removeParent
import com.cc.video.inter.*
import com.cc.video.inter.call.VideoControllerCallListener

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
) : FrameLayout(con, attrs, defStyleAttr, defStyleRes) {
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
        getAllChildView().filter { it is OverGestureListener }.forEach { (it as OverGestureListener).callOverClick() }
        return true
      }

      override fun onDoubleTap(e: MotionEvent?): Boolean {
        getAllChildView().filter { it is OverGestureListener }.forEach { (it as OverGestureListener).callOverDoubleClick() }
        return true
      }

      override fun onDown(e: MotionEvent?): Boolean {
        getAllChildView().filter { it is OverGestureListener }.forEach { (it as OverGestureListener).callOverTouchDown(e) }
        return true
      }

      override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
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
        getAllChildView().filter { it is OverGestureListener }.forEach { (it as OverGestureListener).callOverTouchUp() }
      }
      return true
    }
    return super.onTouchEvent(event)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加OverView">
  fun addOverChildView(v: View) {
    //如果已经存在一样的，则移除之前的
    getAllChildView().firstOrNull { it.javaClass.name == v.javaClass.name }?.removeParent()
    v.removeParent()
    if (v is VideoControllerCallListener) {
      addView(v, 0, ViewGroup.LayoutParams(-1, -1))
    } else {
      addView(v, ViewGroup.LayoutParams(-1, -1))
    }
  }
  //</editor-fold>
}