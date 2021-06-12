package com.cc.base2021.startup

import android.content.Context
import androidx.startup.Initializer
import com.cc.ext.logI
import com.cc.startup.UtilsInit
import com.cc.base2021.config.RxHttpConfig

/**
 * Author:Khaos
 * Date:2020/8/13
 * Time:13:36
 */
class RxhttpInit :Initializer<Int>{
  override fun create(context: Context): Int {
    RxHttpConfig.instance.init()
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(UtilsInit::class.java)
  }

}