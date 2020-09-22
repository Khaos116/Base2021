package com.cc.video.inter

import android.view.MotionEvent

/**
 * 通过统一的Over控件回调手势操作,最主要的功能是：
 * 1.单击:显示和隐藏控制器
 * 2.双击:暂停和播放
 * 3.左右滑动：控制播放进度
 * 4.左边上下滑动：控制亮度
 * 5.右边上下滑动：控制音量
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