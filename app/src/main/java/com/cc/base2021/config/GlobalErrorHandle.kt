package com.cc.base2021.config

import com.blankj.utilcode.util.ActivityUtils
import com.cc.base.ext.toast
import com.cc.base2021.R
import com.cc.base2021.component.login.LoginActivity
import com.cc.base2021.constants.ErrorCode

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/11/19 19:33
 */
class GlobalErrorHandle private constructor() {
  private object SingletonHolder {
    val holder = GlobalErrorHandle()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  var globalErrorCodes = mutableListOf(
      ErrorCode.NO_LOGIN//未登录
  )

  fun dealGlobalErrorCode(errorCode: Int) {
    ActivityUtils.getActivityList()?.firstOrNull()?.let { ac ->
      when (errorCode) {
        //未登录
        ErrorCode.NO_LOGIN -> {
          ac.runOnUiThread {
            ac.toast(R.string.need_login)
            LoginActivity.startActivity(ac)
          }
        }
        else -> {
        }
      }
    }
  }
}