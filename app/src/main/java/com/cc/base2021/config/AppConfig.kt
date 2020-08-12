package com.cc.base2021.config

/**
 * Description: 动态配置
 * @see AppConfig.defaultAppName 代码设置APP名称
 * @author: caiyoufei
 * @date: 2020/3/5 9:41
 */
class AppConfig {
  companion object {
    //是否需要自动登录
    val NEE_AUTO_LOGIN: Boolean = System.currentTimeMillis() > 0L
  }
}