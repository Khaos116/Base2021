package com.cc.video.inter

import android.view.MotionEvent

/**
 * 通过统一的Over控件回调手势操作
 * Author:CASE
 * Date:2020-9-21
 * Time:15:56
 */
interface OverGestureListener {
  fun callOverClick()
  fun callOverDoubleClick()
  fun callOverScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float)
  fun callOverTouchDown(e: MotionEvent?)
  fun callOverTouchUp()
}