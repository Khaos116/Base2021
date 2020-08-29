package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.blankj.utilcode.constant.TimeConstants
import com.cc.base2021.bean.base.BasePageList
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.constants.WanUrls
import rxhttp.delay
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseWan

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/5 18:10
 */
class WanRepository private constructor() {
  private object SingletonHolder {
    val holder = WanRepository()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //获取Banner
  suspend fun banner(readCache: Boolean = true): MutableList<BannerBean> {
    return RxHttp.get(WanUrls.Home.BANNER)
      .setDomainToWanIfAbsent()
      .setCacheValidTime(TimeConstants.DAY.toLong()) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE else CacheMode.ONLY_NETWORK) //请求数据失败读取缓存
      .toResponseWan<MutableList<BannerBean>>()
      .await()
  }

  //获取文章列表
  suspend fun article(
    @IntRange(from = 0) page: Int,
    readCache: Boolean = true
  ): BasePageList<ArticleBean> {
    return RxHttp.get(String.format(WanUrls.Home.ARTICLE, page))
      .setDomainToWanIfAbsent()
      .setCacheValidTime(TimeConstants.DAY.toLong()) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE else CacheMode.ONLY_NETWORK) //请求数据失败读取缓存
      .toResponseWan<BasePageList<ArticleBean>>()
      .await()
  }
}