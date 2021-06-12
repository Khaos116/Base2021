package com.cc.video.inter.call

import android.graphics.Bitmap
import com.cc.video.enu.PlayState
import com.cc.video.enu.PlayUiState

/**
 * 播放器统一对外回调
 * Author:Khaos
 * Date:2020-9-25
 * Time:13:20
 */
interface VideoOverCallListener {
  fun callVideoInfo(url: String, title: String? = "", cover: String? = "")
  fun callDuration(duration: Long)
  fun callFirstFrame()
  fun callVideoSize(width: Int, height: Int)
  fun callPlayState(playState: PlayState)
  fun callPlayProgress(progress: Long)
  fun callBufferProgress(progress: Long)
  fun callBufferPercent(percent: Int, kbps: Float)
  fun callSnapShot(bm: Bitmap, width: Int, height: Int)
  fun callUiState(uiState: PlayUiState)
  fun callEnterPIP()
  fun callOutPIP()
}