package com.cc.ext

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import timber.log.Timber
import java.util.Locale
import java.util.regex.Pattern

/**
 * inline报警告暂不处理，否则打印的地方始终是StringExt不好根据log找到相应的类
 * Author:case
 * Date:2020/8/11
 * Time:17:19
 */
inline fun String?.logE() {
  if (!this.isNullOrBlank()) {
    Timber.e("CASE-$this")
  }
}

inline fun String?.logW() {
  if (!this.isNullOrBlank()) {
    Timber.w("CASE-$this")
  }
}

inline fun String?.logI() {
  if (!this.isNullOrBlank()) {
    Timber.i("CASE-$this")
  }
}

inline fun String?.logD() {
  if (!this.isNullOrBlank()) {
    Timber.d("CASE-$this")
  }
}

inline fun String?.toast() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
    ToastUtils.showShort(this)
  }
}

inline fun String?.isNetImageUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)").matcher(this).matches()
  }
}

inline fun String?.isLiveUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else {
    this.toLowerCase(Locale.getDefault()).run {
      startsWith("rtmp") || startsWith("rtsp")
    }
  }
}