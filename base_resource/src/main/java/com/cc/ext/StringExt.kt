package com.cc.ext

import android.net.Uri
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import timber.log.Timber
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * inline报警告暂不处理，否则打印的地方始终是StringExt不好根据log找到相应的类
 * Author:Khaos
 * Date:2020/8/11
 * Time:17:19
 */
inline fun String?.logE() {
  if (!this.isNullOrBlank()) {
    Timber.e("Khaos-$this")
  }
}

inline fun String?.logW() {
  if (!this.isNullOrBlank()) {
    Timber.w("Khaos-$this")
  }
}

inline fun String?.logI() {
  if (!this.isNullOrBlank()) {
    Timber.i("Khaos-$this")
  }
}

inline fun String?.logD() {
  if (!this.isNullOrBlank()) {
    Timber.d("Khaos-$this")
  }
}

fun String?.toast() {
  if (!this.isNullOrBlank() && AppUtils.isAppForeground()) {
    ToastUtils.showShort(this)
  }
}

fun String?.isNetImageUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(gif|jpeg|png|jpg|bmp)").matcher(this.toLowerCase(Locale.getDefault())).matches()
  }
}

fun String?.isVideoUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else if (!this.toLowerCase(Locale.getDefault()).startsWith("http", true)) {
    false
  } else {
    Pattern.compile(".*?(avi|rmvb|rm|asf|divx|mpg|mpeg|mpe|wmv|mp4|mkv|vob)")
        .matcher(this.toLowerCase(Locale.getDefault())).matches()
  }
}

fun String?.isLiveUrl(): Boolean {
  return if (this.isNullOrEmpty()) {
    false
  } else {
    this.toLowerCase(Locale.getDefault()).run {
      startsWith("rtmp") || startsWith("rtsp")
    }
  }
}

//文件目录转file
fun String?.toFile(): File? {
  if (this != null) {
    return if (this.startsWith("http", true)) null else {
      val f = File(this)
      if (f.exists()) f else UriUtils.uri2File(Uri.parse(this))
    }
  }
  return null
}