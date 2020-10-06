package com.cc.music.receiver

import android.content.*

/**
 * 1.在MusicPlayService中监听音乐控制器相关命令
 * 2.在MusicInit中监听关闭播放服务功能
 * Author:CASE
 * Date:2020-10-5
 * Time:18:59
 */
class OperateReceiver(private val call: (action: String) -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.action?.let { action -> call.invoke(action) }
  }
}