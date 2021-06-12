package com.cc.base2021.app

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.cc.base.app.BaseApplication
import com.cc.ext.logE

/**
 * Author:Khaos
 * Date:2020/8/11
 * Time:15:05
 */
class MyApplication : BaseApplication() {
  //<editor-fold defaultstate="collapsed" desc="通知栏图片加载">
  override fun loadNotificationImage(url: String?, callback: (bit: Bitmap?) -> Unit) {
    if (url.isNullOrBlank()) return
    this.imageLoader.enqueue(ImageRequest.Builder(this).data(url)
        .listener(onError = { _, throwable -> "状态栏封面加载失败:${throwable.message}".logE() })
        .target(onSuccess = { d -> callback.invoke(d.toBitmap()) })
        .build())
  }
  //</editor-fold>
}