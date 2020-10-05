package com.cc.music.enu

/**
 * Author:CASE
 * Date:2020-10-5
 * Time:13:38
 */
enum class PlayController(value: Int) {
  PLAY_PAUSE(0x101),
  PREVIOUS(0x102),
  NEXT(0x103),
  MODE_CHANGE(0x104),
  CLOSE(0x105),
  DETAIL(0x106),
}