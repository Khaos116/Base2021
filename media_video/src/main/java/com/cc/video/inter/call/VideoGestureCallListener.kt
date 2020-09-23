package com.cc.video.inter.call

import com.cc.video.inter.operate.VideoGestureListener

/**
 * 播放器根据状态控制是否能执行手势
 * Author:CASE
 * Date:2020-9-19
 * Time:18:24
 */
interface VideoGestureCallListener {
  fun setCall(call: VideoGestureListener)
  fun callOperate(canOperate: Boolean)
  fun callShowInoperableView(show: Boolean)
}