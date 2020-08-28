package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.blankj.utilcode.constant.TimeConstants
import com.cc.base.ext.isNetImageUrl
import com.cc.base.ext.logE
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.config.RxHttpConfig
import com.cc.base2021.constants.GankUrls
import com.cc.base2021.utils.MMkvUtils
import kotlinx.coroutines.Dispatchers
import okhttp3.Request
import rxhttp.*
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseGank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 18:09
 */
class GankRepository private constructor() {
  //<editor-fold defaultstate="collapsed" desc="单利">
  private object SingletonHolder {
    val holder = GankRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="安卓列表">

  //安卓列表
  suspend fun androidList(
    @IntRange(from = 1) page: Int,
    size: Int = 20,
    readCache: Boolean = true
  ): MutableList<GankAndroidBean> {
    return RxHttp.get(String.format(GankUrls.ANDROID, page, size))
      .setDomainToGankIfAbsent()
      .setCacheValidTime(TimeConstants.DAY.toLong()) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE else CacheMode.ONLY_NETWORK) //请求数据失败读取缓存
      .toResponseGank<MutableList<GankAndroidBean>>()
      //.map { dealAndroidlListUrl(it) }
      //.flowOn(Dispatchers.IO)
      .delay(if (page == 1) 500L else 0L) //为了防止请求太快
      .await()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Girl列表">
  //Girl列表
  suspend fun girlList(
    @IntRange(from = 1) page: Int,
    size: Int = 20,
    readCache: Boolean = true
  ): MutableList<GankGirlBean> {
    return RxHttp.get(String.format(GankUrls.GIRL, page, size))
      .setDomainToGankIfAbsent()
      .setCacheValidTime(TimeConstants.DAY.toLong()) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE else CacheMode.ONLY_NETWORK) //请求数据失败读取缓存
      .toResponseGank<MutableList<GankGirlBean>>()
      //.map { dealGirlListUrl(it) }
      //.flowOn(Dispatchers.IO)
      .delay(if (page == 1) 500L else 0L) //为了防止请求太快
      .await()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始图片地址重定向">

  //处理网络地址
  private suspend fun dealAndroidlListUrl(list: MutableList<GankAndroidBean>): MutableList<GankAndroidBean> {
    //寻找缓存图片地址,过滤掉空地址
    for (bean in list) {
      val images = mutableListOf<String?>()
      bean.images?.filterNotNull()?.forEach { originUrl ->
        if (originUrl.isNotBlank()) {
          if (originUrl.isNetImageUrl()) { //可直接加载的地址
            images.add(originUrl)
          } else { //判断是否有缓存
            val cacheUrl = MMkvUtils.instance.getGankImageUrl(originUrl) //先读取缓存的对应地址
            if (cacheUrl.isNullOrBlank()) { //没有缓存
              val netUrl2 = getImageUrl(originUrl) //获取重定向地址
              if (netUrl2.isNetImageUrl()) MMkvUtils.instance.saveGankImageUrl(originUrl, netUrl2) //获取到重定向地址保存到缓存
              images.add(netUrl2) //重定向成功使用重定向后的地址，重定向失败使用返回的原始地址
            } else images.add(cacheUrl) //使用缓存地址
          }
        }
      }
      bean.images = images
    }
    return list
  }

  //处理网络地址
  private suspend fun dealGirlListUrl(list: MutableList<GankGirlBean>): MutableList<GankGirlBean> {
    //寻找缓存图片地址,过滤掉空地址
    for (bean in list) {
      val images = mutableListOf<String?>()
      bean.images?.filterNotNull()?.forEach { originUrl ->
        if (originUrl.isNotBlank()) {
          if (originUrl.isNetImageUrl()) { //可直接加载的地址
            images.add(originUrl)
          } else { //判断是否有缓存
            val cacheUrl = MMkvUtils.instance.getGankImageUrl(originUrl) //先读取缓存的对应地址
            if (cacheUrl.isNullOrBlank()) { //没有缓存
              val netUrl2 = getImageUrl(originUrl) //获取重定向地址
              if (netUrl2.isNetImageUrl()) MMkvUtils.instance.saveGankImageUrl(originUrl, netUrl2) //获取到重定向地址保存到缓存
              images.add(netUrl2) //重定向成功使用重定向后的地址，重定向失败使用返回的原始地址
            } else images.add(cacheUrl) //使用缓存地址
          }
        }
      }
      bean.images = images
    }
    return list
  }

  //重定向网络图片地址
  @Suppress("BlockingMethodInNonBlockingContext")
  private suspend fun getImageUrl(originUrl: String): String {
    val request = Request.Builder().url(originUrl).build()
    val response = RxHttpConfig.instance.getOkHttpClient().build().newCall(request).execute()
    return if (response.isSuccessful) response.request.url.toString() else originUrl
  }
  //</editor-fold>
}