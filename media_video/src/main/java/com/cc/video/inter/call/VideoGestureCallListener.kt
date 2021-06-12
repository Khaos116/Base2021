package com.cc.video.inter.call

import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState
import com.cc.video.inter.operate.VideoGestureListener

/**
 * 播放器根据状态控制是否能执行手势
 * Author:Khaos
 * Date:2020-9-19
 * Time:18:24
 */
interface VideoGestureCallListener {
  fun callPlayState(state: PlayState)
  fun callUiState(uiState: PlayUiState)
  fun setCall(call: VideoGestureListener)
}