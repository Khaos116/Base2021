package com.cc.base2021.startup

import android.content.Context
import android.os.Build
import androidx.startup.Initializer
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.util.CoilUtils
import com.cc.base2021.config.RxHttpConfig
import com.cc.ext.logI

/**
 * //图片加载配置 https://coil-kt.github.io/coil/getting_started/
 * Author:Khaos
 * Date:2020-12-9
 * Time:14:01
 */
class CoilInit : Initializer<Int> {
  override fun create(context: Context): Int {
    val imageLoader = ImageLoader.Builder(context)
        .crossfade(300)
        .okHttpClient {
          RxHttpConfig.instance.getOkHttpClient()
              .cache(CoilUtils.createDefaultCache(context))
              .build()
        }
        .componentRegistry {
          add(VideoFrameFileFetcher(context))
          add(VideoFrameUriFetcher(context))
          if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder())
          } else {
            add(GifDecoder())
          }
        }
        .build()
    Coil.setImageLoader(imageLoader)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(MMkvInit::class.java)
  }
}