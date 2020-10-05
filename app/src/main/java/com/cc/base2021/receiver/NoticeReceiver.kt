package com.cc.base2021.receiver

import android.annotation.SuppressLint
import android.content.*
import com.blankj.utilcode.util.*
import com.cc.base2021.component.main.MainActivity
import com.cc.ext.logI
import com.cc.music.enu.PlayController

/**
 *
 * Author:CASE
 * Date:2020-10-5
 * Time:18:33
 */
class NoticeReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, it: Intent?) {
    it?.action?.let { ac ->
      if (ac == PlayController.DETAIL.name) {
        if (ActivityUtils.getActivityList().isNullOrEmpty()) {
          "点击通知栏打开APP".logI()
          AppUtils.launchApp(AppUtils.getAppPackageName())
        } else if (ActivityUtils.getActivityList()?.any { it is MainActivity } == true) {
          if (ActivityUtils.getTopActivity() is MainActivity) {
            "MainActivity在顶层不需要再打开".logI()
          } else {
            val intent = Intent(Utils.getApp(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            Utils.getApp().startActivity(intent)
            "将MainActivity置顶".logI()
          }
        } else {
          val intent = Intent(Utils.getApp(), MainActivity::class.java)
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
          Utils.getApp().startActivity(intent)
          "新打开MainActivity".logI()
        }
      }
    }
  }
}