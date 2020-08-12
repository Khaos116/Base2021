package com.cc.base.startup

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import com.blankj.utilcode.util.Utils
import com.cc.base.ext.logE
import com.cc.base.ext.logI
import timber.log.Timber

/**
 * Author:case
 * Date:2020/8/11
 * Time:14:42
 */
class UtilsInit : Initializer<Int> {
  override fun create(context: Context): Int {
    Utils.init(context.applicationContext as Application)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(TimberInit::class.java)
  }
}
