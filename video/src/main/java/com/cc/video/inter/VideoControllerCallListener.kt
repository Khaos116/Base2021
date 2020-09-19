package com.cc.video.inter

/**
 * Author:CASE
 * Date:2020-9-18
 * Time:16:51
 */
interface VideoControllerCallListener {
  fun callPrepare()
  fun callPlay()
  fun callPause()
  fun callStop()
  fun callComplete()
  fun callTitle(title: String)
  fun callBufferProgress(msc: Long)
  fun callProgress(msc: Long)
  fun callDuration(duration: Long)
  fun enterFullScreen()
  fun exitFullScreen()
  fun setCall(call: VideoControllerListener?)
}