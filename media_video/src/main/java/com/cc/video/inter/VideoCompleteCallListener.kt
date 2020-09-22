package com.cc.video.inter

/**
 * Author:CASE
 * Date:2020-9-22
 * Time:18:43
 */
interface VideoCompleteCallListener {
  fun callComplete()
  fun setCall(call: VideoCompleteListener?)
}