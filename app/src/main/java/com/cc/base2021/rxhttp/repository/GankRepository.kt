package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.blankj.utilcode.constant.TimeConstants
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.constants.GankUrls
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseGank

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 18:09
 */
object GankRepository {
  //<editor-fold defaultstate="collapsed" desc="安卓列表">
  //安卓列表
  suspend fun androidList(
      @IntRange(from = 1) page: Int,
      size: Int = 20,
      readCache: Boolean = true
  ): MutableList<GankAndroidBean> {
    return RxHttp.get(String.format(GankUrls.ANDROID, page, size))
        .setDomainToGankIfAbsent()
        .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //读取缓失败存请求数据
        .toResponseGank<MutableList<GankAndroidBean>>()
        .await()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Girl列表">
  //Girl列表
  suspend fun girlList(@IntRange(from = 1) page: Int, size: Int = 20, readCache: Boolean = true): MutableList<GankGirlBean> {
    return RxHttp.get(String.format(GankUrls.GIRL, page, size))
        .setDomainToGankIfAbsent()
        .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //读取缓失败存请求数据
        .toResponseGank<MutableList<GankGirlBean>>()
        .await()
  }
  //</editor-fold>
}