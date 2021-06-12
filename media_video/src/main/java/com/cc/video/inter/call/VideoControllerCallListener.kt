package com.cc.video.inter.call

import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState
import com.cc.video.inter.operate.VideoControllerListener

/**
 * 播放控制器接收播放器相关回调
 * Author:Khaos
 * Date:2020-9-18
 * Time:16:51
 */
interface VideoControllerCallListener {
  fun callPlayState(state: PlayState)
  fun callUiState(uiState: PlayUiState)
  fun callVideoInfo(url: String, title: String? = "", cover: String? = "")
  fun callDuration(duration: Long)
  fun callPlayProgress(progress: Long)
  fun callBufferProgress(progress: Long)
  fun callEnterPIP()
  fun callOutPIP()
  fun setCall(call: VideoControllerListener?)
}