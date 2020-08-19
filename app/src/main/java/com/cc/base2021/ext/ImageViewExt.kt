package com.cc.base2021.ext

import android.widget.ImageView
import coil.Coil
import coil.api.clear
import coil.api.load
import coil.request.LoadRequest
import coil.util.CoilUtils
import com.blankj.utilcode.util.Utils
import com.cc.base.ext.*
import com.cc.base2021.R
import com.cc.base2021.config.RxHttpConfig
import com.cc.base2021.utils.MMkvUtils
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
//普通加载
fun ImageView.loadImg(url: String?) {
  if (url.isNullOrBlank()) {
    this.clear()
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
    this.clear()
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
    this.clear()
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

//加载Gank的图片
fun ImageView.loadGank(url: String?) {
  when {
    url.isNullOrBlank() -> {
      this.clear()
      this.setImageResource(R.drawable.error_720p)
    }
    !MMkvUtils.instance.getGankImageUrl(url).isNullOrBlank() -> {
      this.setTag(R.id.gank_img_url, url)
      //取消之前的请求
      val tagCall = this.getTag(R.id.gank_img_call)
      if (tagCall != null) (tagCall as Call).cancel()
      val sendUrl = MMkvUtils.instance.getGankImageUrl(url)
      "无需重定向直接加载图片:$sendUrl".logD()
      this.loadFullScreen(sendUrl)
    }
    else -> {
      val iv = this
      //取消之前的请求
      val tagCall = iv.getTag(R.id.gank_img_call)
      if (tagCall != null) (tagCall as Call).cancel()
      iv.clear()
      iv.setImageResource(R.drawable.loading_720p)
      //请求真正的加载地址
      val request = Request.Builder().url(url).build()
      val call = RxHttpConfig.instance.getOkHttpClient().build().newCall(request)
      //设置tag
      iv.setTag(R.id.gank_img_url, url)
      iv.setTag(R.id.gank_img_call, call)
      call.enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
          //不是自动取消才加载失败的图
          if (!call.isCanceled()) GlobalScope.launch(Dispatchers.Main) {
            iv.clear()
            iv.setImageResource(R.drawable.error_720p)
          }
        }

        override fun onResponse(call: Call, response: Response) {
          MMkvUtils.instance.saveGankImageUrl(url, response.request.url.toString())
          //确保没有被复用才进行加载
          val tag = iv.getTag(R.id.gank_img_url)
          if (tag != null && tag == url) {
            "重定向前:${url},重定向后加载图片:${response.request.url.toString()}".logI()
            iv.loadFullScreen(response.request.url.toString())
          }
          response.close()
        }
      })
    }
  }
}