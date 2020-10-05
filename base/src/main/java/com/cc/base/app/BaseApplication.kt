package com.cc.base.app

import android.content.Context
import androidx.multidex.MultiDex
import com.cc.ResourceApplication

/**
 * Author:case
 * Date:2020/8/11
 * Time:14:29
 */
abstract class BaseApplication : ResourceApplication() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }
}