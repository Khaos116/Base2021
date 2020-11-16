package com.cc.base2021.ext

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.widget.ImageView
import coil.*
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher
import coil.request.ImageRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.*
import com.cc.base2021.R
import com.cc.base2021.config.AppConfig
import com.cc.ext.launchError
import com.cc.ext.logE
import com.cc.utils.MediaMetadataRetrieverUtils
import com.cc.utils.MediaUtils
import kotlinx.coroutines.*
import okhttp3.Cache
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.File

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
//正方形图片加载s
inline fun ImageView.loadImgSquare(url: String?) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.setImageResource(R.drawable.error_square)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.load(url, context.imageLoader) {
      crossfade(true)
      placeholder(R.drawable.loading_square)
      error(R.drawable.error_square)
      listener { _, _ -> iv.setTag(R.id.suc_img, url) }
    }
  }
}

//横向图片加载
fun ImageView.loadImgHorizontal(url: String?,
    loadingScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    sucScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.scaleType = loadingScaleType
    this.setImageResource(R.drawable.error_720p_horizontal)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.scaleType = loadingScaleType
    iv.load(url, context.imageLoader) {
      crossfade(true)
      placeholder(R.drawable.loading_720p_horizontal)
      error(R.drawable.error_720p_horizontal)
      listener { _, _ ->
        iv.scaleType = sucScaleType
        iv.setTag(R.id.suc_img, url)
      }
    }
  }
}

//竖向图片加载
fun ImageView.loadImgVerticalScreen(url: String?,
    loadingScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER,
    sucScaleType: ImageView.ScaleType = ImageView.ScaleType.FIT_CENTER) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.scaleType = loadingScaleType
    this.setImageResource(R.drawable.error_720p_vertical)
  } else {
    if (getTag(R.id.suc_img) == url) return
    val iv = this
    iv.scaleType = loadingScaleType
    iv.load(url, context.imageLoader) {
      crossfade(true)
      placeholder(R.drawable.loading_720p_vertical)
      error(R.drawable.error_720p_vertical)
      listener(onError = { r, e -> "图片加载失败:${r.data},e=${e.message ?: "null"}".logE() }) { _, _ ->
        iv.scaleType = sucScaleType
        iv.setTag(R.id.suc_img, url)
      }
    }
  }
}

fun ImageView.clearLoad() {
  this.clear()
  setTag(R.id.suc_img, null)
}

//加载缓存文件
fun ImageView.loadCacheFileFullScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.clearLoad()
    this.setImageResource(R.drawable.error_720p_vertical)
  } else {
    url.toHttpUrlOrNull()?.let { u ->
      val f = CoilUtils.createDefaultCache(Utils.getApp()).directory.listFiles().orEmpty().find { it.name.contains(Cache.key(u)) }
      if (f?.exists() == true) { //文件存在直接加载
        this.load(f, context.imageLoader)
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

//加载视频网络封面(type:0-方形，1-横向，2-竖向)
fun ImageView.loadNetVideoCover(url: String?, type: Int = 0) {
  url?.let {
    val cacheFile = File(AppConfig.VIDEO_OVER_CACHE_DIR, EncryptUtils.encryptMD5ToString(it))
    if (cacheFile.exists()) {
      load(cacheFile)
      return
    }
    getTag(R.id.id_retriever)?.let { r -> (r as MediaMetadataRetriever).release() }
    when (type) {
      1 -> setImageResource(R.drawable.loading_720p_horizontal)
      2 -> setImageResource(R.drawable.loading_720p_vertical)
      else -> setImageResource(R.drawable.loading_square)
    }
    val retriever = MediaMetadataRetriever()
    setTag(R.id.id_retriever, retriever)
    MediaMetadataRetrieverUtils.getNetVideoCover(retriever, cacheFile, it) { bit ->
      setTag(R.id.id_retriever, null)
      if (bit != null) setImageBitmap(bit) else {
        when (type) {
          1 -> setImageResource(R.drawable.error_720p_horizontal)
          2 -> setImageResource(R.drawable.error_720p_vertical)
          else -> setImageResource(R.drawable.error_square)
        }
      }
    }
  }
}