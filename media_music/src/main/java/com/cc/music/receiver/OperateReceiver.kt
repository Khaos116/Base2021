package com.cc.music.receiver

import android.content.*

/**
 * Author:CASE
 * Date:2020-10-5
 * Time:18:59
 */
class OperateReceiver(private val call: (action: String) -> Unit) : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    intent?.action?.let { action -> call.invoke(action) }
  }
}