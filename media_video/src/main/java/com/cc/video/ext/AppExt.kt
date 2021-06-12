package com.cc.video.ext

import android.app.Application

/**
 * Author:Khaos
 * Date:2020-9-19
 * Time:16:13
 */
private var userMobile: Boolean = false
var Application.useMobileNet: Boolean
  get() {
    return userMobile
  }
  set(value) {
    userMobile = value
  }