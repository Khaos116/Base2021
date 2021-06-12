package com.cc.video.inter.call

import com.cc.video.enu.PlayState
import com.cc.video.inter.operate.VideoCompleteListener

/**
 * Author:Khaos
 * Date:2020-9-22
 * Time:18:43
 */
interface VideoCompleteCallListener {
  fun callPlayState(state: PlayState)
  fun setCall(call: VideoCompleteListener?)
}