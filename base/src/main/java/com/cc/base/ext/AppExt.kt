package com.cc.base.ext

import com.blankj.utilcode.util.Utils
import com.cc.base.app.BaseApplication

/**
 * Author:CASE
 * Date:2020-9-22
 * Time:18:21
 */
//获取BaseApplication
fun Utils.getApplication(): BaseApplication {
  return Utils.getApp() as BaseApplication
}