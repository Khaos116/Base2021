package com.cc.base2021.rxhttp.repository

import androidx.annotation.IntRange
import com.blankj.utilcode.constant.TimeConstants
import com.cc.base2021.bean.base.BasePageList
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.constants.WanUrls
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.param.toResponseWan

/**
 * Description:
 * @author: Khaos
 * @date: 2020/3/5 18:10
 */
object WanRepository {
  //获取Banner
  suspend fun banner(readCache: Boolean = true): MutableList<BannerBean> {
    return RxHttp.get(WanUrls.Home.BANNER)
      .setDomainToWanIfAbsent()
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //读取缓失败存请求数据
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
      .setCacheMode(if (readCache) CacheMode.READ_CACHE_FAILED_REQUEST_NETWORK else CacheMode.ONLY_NETWORK) //读取缓失败存请求数据
      .toResponseWan<BasePageList<ArticleBean>>()
      .await()
  }
}
