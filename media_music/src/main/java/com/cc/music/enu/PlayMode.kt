package com.cc.music.enu

/**
 * Author:Khaos
 * Date:2020-10-1
 * Time:15:11
 */
enum class PlayMode(value: Int) {
  LOOP_ALL(0), //全部循环
  LOOP_ONE(1), //单曲循环
  PLAY_IN_ORDER(2), //顺序播放
  PLAY_RANDOM(3), //随机播放
}