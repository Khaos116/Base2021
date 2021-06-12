package com.cc.video.enu

/**
 * Author:Khaos
 * Date:2020-9-23
 * Time:14:14
 */
enum class PlayState {
  SET_DATA, //设置数据
  SHOW_MOBILE, //显示手机网络
  PREPARING, //准备中
  PREPARED, //准备完成
  BUFFING, //缓存中
  BUFFED, //缓冲完成
  SEEKING, //Seek中
  SEEKED, //Seek完成
  START, //开始播放
  PAUSE, //暂停播放
  STOP, //停止播放
  COMPLETE, //播放结束
  ERROR //播放出错
}