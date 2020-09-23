package com.cc.video.inter.operate

import androidx.annotation.FloatRange
import androidx.annotation.IntRange

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
  fun seekPreviewTo(@IntRange(from = 0) msc: Long)
  fun seekTo(@IntRange(from = 0) msc: Long)
  fun setVolume(@FloatRange(from = 0.0, to = 1.0) volume: Float)
  fun setPlayerVolume(@FloatRange(from = 0.0, to = 1.0) volume: Float)
  fun setBright(@FloatRange(from = 0.0, to = 1.0) bright: Float)
}