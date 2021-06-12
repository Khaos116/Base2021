package com.cc.music.enu

/**
 * Author:Khaos
 * Date:2020-10-1
 * Time:14:29
 */
enum class PlayState(value: Int) {
  SET_DATA(0),
  PREPARING(1),
  PREPARED(2),
  START(3),
  PAUSE(4),
  STOP(5),
  COMPLETE(6),
  ERROR(7),
  BUFFING(8),
  BUFFED(9),
  SEEKING(10),
  SEEKED(11),
}