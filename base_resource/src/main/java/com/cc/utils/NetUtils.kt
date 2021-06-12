package com.cc.utils

import android.annotation.SuppressLint
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.R
import com.cc.ext.toast

/**
 * Author:Khaos
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