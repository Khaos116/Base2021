package com.cc.base2021.startup

import android.content.Context
import androidx.startup.Initializer
import com.cc.ext.logI

/**
 * APP最后一个初始化，添加新的放到本初始化的依赖里面即可
 * Author:CASE
 * Date:2020-9-17
 * Time:13:49
 */
class AppLastInit : Initializer<Int> {
  override fun create(context: Context): Int {
    "App全部初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(MMkvInit::class.java, SwipeInit::class.java)
  }
}