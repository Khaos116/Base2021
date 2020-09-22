package com.cc.base2021.ext

import android.widget.ImageView
import coil.*
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.Utils
import com.cc.base2021.R
import com.cc.ext.logE
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
//正方形图片加载
inline fun ImageView.loadImgSquare(url: String?) {
  if (url.isNullOrBlank()) {
    this.clear()
    this.setImageResource(R.drawable.error_square)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_square)
      error(R.drawable.error_square)
      listener { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
  }
}

//横向图片加载
fun ImageView.loadImgHorizontal(url: String?) {
  if (url.isNullOrBlank()) {
    this.clear()
    this.setImageResource(R.drawable.error_720p_horizontal)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_720p_horizontal)
      error(R.drawable.error_720p_horizontal)
      listener { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
  }
}

//竖向图片加载
fun ImageView.loadImgVerticalScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.clear()
    this.setImageResource(R.drawable.error_720p_vertical)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_720p_vertical)
      error(R.drawable.error_720p_vertical)
      listener { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
  }
}

//加载缓存文件
fun ImageView.loadCacheFileFullScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.clear()
    this.setImageResource(R.drawable.error_720p_vertical)
  } else {
    url.toHttpUrlOrNull()?.let { u ->
      val f = CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles().orEmpty().find { it.name.contains(Cache.key(u)) }
      if (f?.exists() == true) { //文件存在直接加载
        this.load(f)
      } else { //文件不存在，进行下载
        Coil.imageLoader(Utils.getApp()).enqueue(
          ImageRequest.Builder(Utils.getApp()).data(u).target(
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