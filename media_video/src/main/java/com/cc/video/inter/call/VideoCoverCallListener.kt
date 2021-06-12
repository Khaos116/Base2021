package com.cc.video.inter.call

import com.cc.video.enu.PlayState
import com.cc.video.inter.operate.VideoCoverListener

/**
 * Author:Khaos
 * Date:2020-9-23
 * Time:10:09
 */
interface VideoCoverCallListener {
  fun callPlayState(state: PlayState)
  fun callVideoInfo(url: String, title: String? = "", cover: String? = "")
  fun setCall(call: VideoCoverListener?)
}