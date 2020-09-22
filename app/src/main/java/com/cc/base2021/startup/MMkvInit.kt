package com.cc.base2021.startup

import android.content.Context
import androidx.startup.Initializer
import com.cc.ext.logI
import com.tencent.mmkv.MMKV

/**
 * Author:case
 * Date:2020/8/12
 * Time:16:08
 */
class MMkvInit : Initializer<Int> {
  override fun create(context: Context): Int {
    MMKV.initialize(context)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(RxhttpInit::class.java)
  }

}