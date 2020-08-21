package com.cc.base.utils

import android.annotation.SuppressLint
import android.content.Context
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.base.R
import com.cc.base.ext.toast

/**
 * Author:case
 * Date:2020/8/21
 * Time:20:18
 */
class NetUtils {
  companion object {
    @SuppressLint("MissingPermission")
    fun checkNetToast(): Boolean {
      return try {
        if (NetworkUtils.isConnected()) {
          true
        } else {
          StringUtils.getString(R.string.net_error).toast()
          false
        }
      } catch (e: Exception) {
        e.printStackTrace()
        false
      }
    }
  }
}