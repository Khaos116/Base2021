package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.cc.base2021.bean.base.BasePageList
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.constants.TimeConstants
import com.cc.base2021.constants.WanUrls
import com.cc.base2021.utils.RxUtils
import io.reactivex.Observable
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp

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
  fun banner(readCache: Boolean = true): Observable<MutableList<BannerBean>> {
    return RxHttp.get(WanUrls.Home.BANNER)
      .setDomainToWanIfAbsent()
      .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //先读取缓存，失败再请求数据
      .asResponseWanList(BannerBean::class.java)
      .compose(RxUtils.instance.rx2SchedulerHelperODelay())
  }

  //获取文章列表
  fun article(
    @IntRange(from = 0) page: Int,
    readCache: Boolean = true
  ): Observable<BasePageList<ArticleBean>> {
    return RxHttp.get(String.format(WanUrls.Home.ARTICLE, page))
      .setDomainToWanIfAbsent()
      .setCacheValidTime(TimeConstants.HOME_CACHE) //设置缓存时长
      .setCacheMode(
        if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK
        else CacheMode.ONLY_NETWORK
      ) //先读取缓存，失败再请求数据
      .asResponseWanBasePageList(ArticleBean::class.java)
      .compose(RxUtils.instance.rx2SchedulerHelperODelay())
  }
}