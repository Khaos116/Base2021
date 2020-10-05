package com.cc.base2021.app

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.*
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.cc.base.app.BaseApplication
import com.cc.base2021.config.RxHttpConfig
import com.cc.ext.logE

/**
 * Author:case
 * Date:2020/8/11
 * Time:15:05
 */
class MyApplication : BaseApplication(), ImageLoaderFactory {
  //图片加载配置 https://coil-kt.github.io/coil/getting_started/
  override fun newImageLoader(): ImageLoader {
    //当使用load加载图片时，如果需要setImageResource/setImageDrawable则应该先调用clear
    return ImageLoader.Builder(this)
        .crossfade(true)
        .okHttpClient {
          RxHttpConfig.instance.getOkHttpClient()
              .cache(CoilUtils.createDefaultCache(this))
              .build()
        }
        .build()
  }

  override fun loadNotificationImage(url: String?, callback: (bit: Bitmap?) -> Unit) {
    if (url.isNullOrBlank()) return
    Coil.imageLoader(this)
        .enqueue(ImageRequest.Builder(this).data(url)
            .listener(onError = { _, throwable -> "封面加载失败:${throwable.message}".logE() })
            .target(onSuccess = { d -> callback.invoke(d.toBitmap()) }).build()) //callback.invoke(it.toBitmap())

  }
}