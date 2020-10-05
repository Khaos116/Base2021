package com.cc

import android.app.Application
import android.graphics.Bitmap

/**
 * Author:CASE
 * Date:2020-10-5
 * Time:15:58
 */
abstract class ResourceApplication : Application() {
  //通知栏图片加载
  abstract fun loadImage(url: String?, callback: (bit: Bitmap?) -> Unit)
}