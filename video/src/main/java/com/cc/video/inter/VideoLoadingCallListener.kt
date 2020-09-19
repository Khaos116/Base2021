package com.cc.video.inter

/**
 * Author:CASE
 * Date:2020-9-19
 * Time:10:20
 */
interface VideoLoadingCallListener {
  fun showLoading()
  fun hiddenLoading()
  fun showBuffer(percent: Int, kbps: Float)
}