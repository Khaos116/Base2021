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
    videoView.setOverView(VideoOverView(mContext).apply {
      addOverChildView(VideoGestureView(mContext).apply { setLiveVideo(false) })
      addOverChildView(VideoControllerView(mContext).apply { setLiveVideo(false) })
      addOverChildView(VideoLoadingView(mContext))
      addOverChildView(VideoErrorView(mContext).apply { setLiveVideo(false) })
      addOverChildView(VideoCompleteView(mContext).apply { setLiveVideo(false) })
      addOverChildView(object : VideoCoverView(mContext) {
        override fun loadVideoCover(url: String, iv: ImageView) = callCover.invoke(url, iv)
      })
    })
  }

  fun userLiveController(videoView: AliVideoView, callCover: (url: String?, iv: ImageView) -> Unit) {
    val mContext = videoView.context
    videoView.setOverView(VideoOverView(mContext).apply {
      addOverChildView(VideoGestureView(mContext).apply { setLiveVideo(true) })
      addOverChildView(VideoControllerView(mContext).apply { setLiveVideo(true) })
      addOverChildView(VideoLoadingView(mContext))
      addOverChildView(VideoErrorView(mContext).apply { setLiveVideo(true) })
      addOverChildView(VideoCompleteView(mContext).apply { setLiveVideo(true) })
      addOverChildView(object : VideoCoverView(mContext) {
        override fun loadVideoCover(url: String, iv: ImageView) = callCover.invoke(url, iv)
      })
    })
  }
}