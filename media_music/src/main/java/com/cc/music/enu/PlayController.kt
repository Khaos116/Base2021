package com.cc.music.enu

/**
 * 1.在MusicPlayService中监听音乐控制器相关命令
 * 2.在MusicInit中监听关闭播放服务功能
 * 3.在APP主项目中需要监听DETAIL打开详情页面(为了关闭通知栏，使用了通过IntentActivity中转命令的方式)
 * Author:Khaos
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