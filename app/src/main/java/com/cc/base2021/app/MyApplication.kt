package com.cc.base2021.app

import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.CoilUtils
import com.cc.base.app.BaseApplication
import com.cc.base2021.config.RxHttpConfig

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
}