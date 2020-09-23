package com.cc.video.inter.operate

/**
 * 播放控制器发送相关操作
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
  fun lock()
  fun unlock()
  fun fullScreenOrExit()
}