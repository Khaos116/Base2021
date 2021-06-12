package com.cc.base.app

import android.content.Context
import androidx.multidex.MultiDex
import com.cc.ResourceApplication
import me.jessyan.autosize.AutoSizeConfig

/**
 * Author:Khaos
 * Date:2020/8/11
 * Time:14:29
 */
abstract class BaseApplication : ResourceApplication() {
  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
    MultiDex.install(this)
    //字体sp不跟随系统大小变化
    AutoSizeConfig.getInstance().isExcludeFontScale = true
  }
}