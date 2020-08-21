package com.cc.base2021.component.main.viewmodel

import androidx.lifecycle.*
import com.cc.base.viewmodel.BaseViewModel
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.rxhttp.repository.GankRepository

/**
 * Author:case
 * Date:2020/8/21
 * Time:9:52
 */
class GankViewModel : BaseViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val androidState: LiveData<ListUiState<MutableList<GankAndroidBean>>>
    get() = androidList

  //刷新
  fun refresh() {
    if (isRequest) return
    requestAndroidList(1)
  }

  //加载更多
  fun loadMore() {
    if (isRequest) return
    requestAndroidList(currentPage + 1)
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private val androidList = MutableLiveData<ListUiState<MutableList<GankAndroidBean>>>()
  private var isRequest = false
  private var currentPage = 1
  private var pageSize = 10
  private var hasMore = true
  private fun requestAndroidList(page: Int) {
    rxLifeScope.launch({
      //协程代码块
      val result = GankRepository.instance.androidList(page = page, size = pageSize)
      currentPage = page
      hasMore = result.size == pageSize
      //加载成功先关闭弹窗,再设置数据，方便填充空View的情况
      //可以直接更新UI
      androidList.value = ListUiState(
        suc = true,
        hasMore = hasMore,
        data = if (page == 1) result else ((androidList.value?.data ?: mutableListOf()) + result).toMutableList()
      )
    }, { e -> //异常回调，这里可以拿到Throwable对象
      androidList.value = ListUiState(
        exc = e,
        hasMore = hasMore,
        data = androidList.value?.data ?: mutableListOf()
      )
    }, { //开始回调，可以开启等待弹窗
      isRequest = true
      androidList.value = ListUiState(
        isLoading = true,
        hasMore = hasMore,
        data = androidList.value?.data ?: mutableListOf()
      )
    }, { //结束回调，可以销毁等待弹窗
      isRequest = false
    })
  }
  //</editor-fold>
}