package com.cc.base2021.startup

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import androidx.startup.Initializer
import com.cc.base2021.receiver.NoticeReceiver
import com.cc.base2021.utils.IMMLeaks
import com.cc.ext.logI
import com.cc.music.enu.PlayController
import com.cc.music.service.MusicPlayService

/**
 * APP最后一个初始化，添加新的放到本初始化的依赖里面即可
 * Author:CASE
 * Date:2020-9-17
 * Time:13:49
 */
class AppLastInit : Initializer<Int> {
  override fun create(context: Context): Int {
    val action = PlayController.DETAIL.name
    context.registerReceiver(NoticeReceiver(), IntentFilter(MusicPlayService::class.java.name).apply { addAction(action) })
    IMMLeaks.fixFocusedViewLeak(context.applicationContext as Application)
    "App初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(CoilInit::class.java, SwipeInit::class.java, SmartInit::class.java)
  }
}