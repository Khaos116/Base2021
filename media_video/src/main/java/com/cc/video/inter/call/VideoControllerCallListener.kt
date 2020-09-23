package com.cc.video.inter.call

import com.cc.video.inter.operate.VideoControllerListener

/**
 * 播放控制器接收播放器相关回调
 * Author:CASE
 * Date:2020-9-18
 * Time:16:51
 */
interface VideoControllerCallListener {
  fun callShowInoperableView(show: Boolean)
  fun callPrepare()
  fun callPlay()
  fun callPause()
  fun callStop()
  fun callComplete()
  fun unLock()
  fun callTitle(title: String)
  fun callBufferProgress(msc: Long)
  fun callProgress(msc: Long)
  fun callDuration(duration: Long)
  fun enterFullScreen()
  fun exitFullScreen()
  fun callOperate(canOperate: Boolean)
  fun setCall(call: VideoControllerListener?)
}