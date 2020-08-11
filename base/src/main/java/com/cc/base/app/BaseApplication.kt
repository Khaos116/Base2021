package com.cc.base.app

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex

/**
 * Author:case
 * Date:2020/8/11
 * Time:14:29
 */
abstract class BaseApplication : Application() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }
}