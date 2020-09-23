package com.cc.video.inter.call

import com.cc.video.inter.operate.VideoErrorListener

/**
 * 播放器回调播放状态
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