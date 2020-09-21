package com.cc.video.inter

/**
 * 播放器根据加载情况显示loading等
 * Author:CASE
 * Date:2020-9-19
 * Time:10:20
 */
interface VideoLoadingCallListener {
  fun showLoading()
  fun hiddenLoading()
  fun showBuffer(percent: Int, kbps: Float)
}