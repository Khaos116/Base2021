package com.cc.video.inter.call

import com.cc.video.enu.PlayState

/**
 * 播放器根据加载情况显示loading等
 * Author:Khaos
 * Date:2020-9-19
 * Time:10:20
 */
interface VideoLoadingCallListener {
  fun callPlayState(state: PlayState)
  fun callBufferPercent(percent: Int, kbps: Float)
}