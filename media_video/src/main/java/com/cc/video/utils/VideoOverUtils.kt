package com.cc.video.utils

import android.widget.ImageView
import com.cc.video.ui.*

/**
 * Author:CASE
 * Date:2020-9-23
 * Time:10:44
 */
class VideoOverUtils private constructor() {
  private object SingletonHolder {
    val holder = VideoOverUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  fun userStandardController(videoView: AliVideoView, callCover: (url: String?, iv: ImageView) -> Unit) {
    val mContext = videoView.context
    videoView.addOverView(VideoGestureView(mContext).apply { setLiveVideo(false) })
    videoView.addOverView(VideoControllerView(mContext).apply { setLiveVideo(false) })
    videoView.addOverView(VideoLoadingView(mContext))
    videoView.addOverView(VideoErrorView(mContext).apply { setLiveVideo(false) })
    videoView.addOverView(VideoCompleteView(mContext).apply { setLiveVideo(false) })
    videoView.addOverView(object : VideoCoverView(mContext) {
      override fun loadVideoCover(url: String, iv: ImageView) {
        callCover.invoke(url, iv)
      }
    })
  }

  fun userLiveController(videoView: AliVideoView, callCover: (url: String?, iv: ImageView) -> Unit) {
    val mContext = videoView.context
    videoView.addOverView(VideoGestureView(mContext).apply { setLiveVideo(true) })
    videoView.addOverView(VideoControllerView(mContext).apply { setLiveVideo(true) })
    videoView.addOverView(VideoLoadingView(mContext))
    videoView.addOverView(VideoErrorView(mContext).apply { setLiveVideo(true) })
    videoView.addOverView(VideoCompleteView(mContext).apply { setLiveVideo(true) })
    videoView.addOverView(object : VideoCoverView(mContext) {
      override fun loadVideoCover(url: String, iv: ImageView) {
        callCover.invoke(url, iv)
      }
    })
  }
}