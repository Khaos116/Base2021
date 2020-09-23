package com.cc.video.inter.call

import com.cc.video.inter.operate.VideoCoverListener

/**
 * Author:CASE
 * Date:2020-9-23
 * Time:10:09
 */
interface VideoCoverCallListener {
  fun callCoverUrl(url: String?)
  fun callShowAllView()
  fun callShowPlayView()
  fun callHiddenView()
  fun setCall(call: VideoCoverListener?)
}