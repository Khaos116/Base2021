package com.cc.video.inter

/**
 * 播放手势相关操作
 * Author:CASE
 * Date:2020-9-19
 * Time:18:01
 */
interface VideoGestureListener {
  //获取
  fun getDuration(): Long
  fun getCurrentPosition(): Long
  fun getVolumeCurrent(): Float
  fun getVolumeMax(): Float
  fun getVolumeMin(): Float
  fun getBrightCurrent(): Float
  fun getBrightMax(): Float
  fun getBrightMin(): Float

  //设置
  fun onPause()
  fun onStart()
  fun seekPreviewTo(msc: Long)
  fun seekTo(msc: Long)
  fun setVolume(volume: Float)
  fun setBright(bright: Float)
}