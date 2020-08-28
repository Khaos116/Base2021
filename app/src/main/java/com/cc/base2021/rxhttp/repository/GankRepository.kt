package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.blankj.utilcode.constant.TimeConstants
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.constants.GankUrls
import rxhttp.delay
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseGank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 18:09
 */
class GankRepository private constructor() {
  private object SingletonHolder {
    val holder = GankRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

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
      .delay(if (page == 1) 500L else 0L) //为了防止请求太快
      .await()
  }

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
      .delay(if (page == 1) 500L else 0L) //为了防止请求太快
      .await()
  }
}