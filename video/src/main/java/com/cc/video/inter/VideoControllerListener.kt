package com.cc.video.inter

/**
 * Author:CASE
 * Date:2020-9-18
 * Time:16:28
 */
interface VideoControllerListener {
  fun onBack()
  fun onPlayOrPause()
  fun onPlay()
  fun onPause()
  fun seekTo(msc: Long)
  fun onStop()
  fun fullScreenOrExit()
  fun setControllerCall(call: VideoControllerCallListener?)
}