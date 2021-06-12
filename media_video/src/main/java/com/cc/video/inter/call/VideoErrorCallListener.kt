package com.cc.video.inter.call

import com.cc.video.enu.PlayState
import com.cc.video.inter.operate.VideoErrorListener

/**
 * 播放器回调播放状态
 * Author:Khaos
 * Date:2020-9-19
 * Time:10:21
 */
interface VideoErrorCallListener {
  fun callPlayState(state: PlayState)
  fun setCall(call: VideoErrorListener)
}