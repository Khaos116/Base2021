package com.cc.base2021.ext

import android.widget.ImageView
import coil.Coil
import coil.api.load
import coil.request.LoadRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.Utils
import com.cc.base.ext.logE
import com.cc.base2021.R
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
//普通加载
fun ImageView.load(url: String?) {
  if (url.isNullOrBlank()) {
    this.setImageResource(R.drawable.error_square)
  } else {
    this.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_square)
      error(R.drawable.error_square)
    }
  }
}

//全屏加载
fun ImageView.loadFullScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.setImageResource(R.drawable.error_720p)
  } else {
    this.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_720p)
      error(R.drawable.error_720p)
    }
  }
}

//加载缓存文件
fun ImageView.loadCacheFileFullScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.setImageResource(R.drawable.error_720p)
  } else {
    url.toHttpUrlOrNull()?.let { u ->
      val f = CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles().orEmpty().find { it.name.contains(Cache.key(u)) }
      if (f?.exists() == true) { //文件存在直接加载
        this.load(f)
      } else { //文件不存在，进行下载
        Coil.imageLoader(Utils.getApp()).execute(
          LoadRequest.Builder(Utils.getApp()).data(u).target(
            onStart = {
              "缓存图片开始下载".logE()
            },
            onSuccess = {
              "缓存图片下载成功".logE()
            },
            onError = {
              "缓存图片下载失败:${u}".logE()
            }
          ).build()
        )
      }
    }
  }
}