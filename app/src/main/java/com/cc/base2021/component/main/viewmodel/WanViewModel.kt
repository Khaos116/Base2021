package com.cc.base2021.component.main.viewmodel

import androidx.lifecycle.*
import com.cc.base.viewmodel.BaseViewModel
import com.cc.base2021.bean.base.BasePageList
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.rxhttp.repository.WanRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

/**
 * Author:case
 * Date:2020/8/29
 * Time:17:18
 */
class WanViewModel : BaseViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val articleState: LiveData<ListUiState<MutableList<ArticleBean>>>
    get() = articleList
  val bannerState: LiveData<MutableList<BannerBean>>
    get() = bannerList

  //刷新
  fun refresh() {
    if (isRequest) return
    requestWanList(0)
  }

  //加载更多
  fun loadMore() {
    if (isRequest) return
    requestWanList(currentPage + 1)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private val articleList = MutableLiveData<ListUiState<MutableList<ArticleBean>>>()
  private val bannerList = MutableLiveData<MutableList<BannerBean>>()
  private var isRequest = false
  private var currentPage = 0
  private var hasMore = true

  private fun requestWanList(page: Int) {
    rxLifeScope.launch({
      val resultArticle = async { WanRepository.instance.article(page) }
      var articleTemp = BasePageList<ArticleBean>()
      //协程代码块
      if (page == 1) {
        val resultBanner = async { WanRepository.instance.banner() }
        val bannerTemp = resultBanner.await()
        articleTemp = resultArticle.await()
        bannerList.value = bannerTemp
      } else {
        articleTemp = resultArticle.await()
      }
      val result = articleTemp.datas?.toMutableList() ?: mutableListOf()
      hasMore = articleTemp.curPage < articleTemp.total
      currentPage = page
      //可以直接更新UI
      articleList.value = ListUiState(
        suc = true,
        hasMore = hasMore,
        data = if (page == 1) result else ((articleList.value?.data ?: mutableListOf()) + result).toMutableList()
      )
    }, { e -> //异常回调，这里可以拿到Throwable对象
      articleList.value = ListUiState(
        exc = e,
        hasMore = hasMore,
        data = articleList.value?.data ?: mutableListOf()
      )
    }, { //开始回调，可以开启等待弹窗
      isRequest = true
      articleList.value = ListUiState(
        isLoading = true,
        hasMore = hasMore,
        data = articleList.value?.data ?: mutableListOf()
      )
    }, { //结束回调，可以销毁等待弹窗
      isRequest = false
    })
  }
  //</editor-fold>
}