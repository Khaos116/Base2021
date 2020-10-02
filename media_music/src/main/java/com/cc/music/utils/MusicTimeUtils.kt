package com.cc.music.utils

import java.util.Formatter
import java.util.Locale

/**
 * Author:CASE
 * Date:2020-9-19
 * Time:19:03
 */
class MusicTimeUtils private constructor() {
  private object SingletonHolder {
    val holder = MusicTimeUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //<editor-fold defaultstate="collapsed" desc="时间处理">
  private var formatterBuilder: StringBuilder = StringBuilder()
  private var formatterHHMM: Formatter = Formatter(formatterBuilder, Locale.getDefault())
  fun forMatterMusicTime(timeMillSeconds: Long): String {
    val totalSeconds: Int = (timeMillSeconds / 1000).toInt()
    val seconds: Int = totalSeconds % 60
    val minutes: Int = totalSeconds / 60 % 60
    val hours: Int = totalSeconds / 3600
    formatterBuilder.setLength(0)
    return if (hours > 0) {
      formatterHHMM.format("%d:%02d:%02d", hours, minutes, seconds).toString()
    } else {
      formatterHHMM.format("%02d:%02d", minutes, seconds).toString()
    }
  }

  fun forMatterMusicTimeSeek(timeSeconds: Long): String {
    val totalSeconds: Int = timeSeconds.toInt()
    val seconds: Int = totalSeconds % 60
    val minutes: Int = totalSeconds / 60 % 60
    val hours: Int = totalSeconds / 3600
    formatterBuilder.setLength(0)
    return when {
      hours > 0 -> formatterHHMM.format("%dh%dm%ds", hours, minutes, seconds).toString()
      minutes > 0 -> formatterHHMM.format("%dm%ds", minutes, seconds).toString()
      else -> formatterHHMM.format("%ds", seconds).toString()
    }
  }
  //</editor-fold>
}