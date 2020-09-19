package com.cc.video.inter

/**
 * Author:CASE
 * Date:2020-9-19
 * Time:10:21
 */
interface VideoErrorCallListener {
  fun errorNormal()
  fun errorMobileNet()
  fun errorNoNet()
  fun setCall(call: VideoErrorListener)
}