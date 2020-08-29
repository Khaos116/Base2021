package com.cc.base2021.component.main.viewmodel

import androidx.lifecycle.*
import com.cc.base.viewmodel.BaseViewModel
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.rxhttp.repository.GankRepository

/**
 * https://juejin.im/post/6844904100090347528#heading-1
 * Author:case
 * Date:2020/8/13
 * Time:9:48
 */
class GirlViewModel : BaseViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val girlState: LiveData<ListUiState<MutableList<GankGirlBean>>>
    get() = girlList

  //刷新
  fun refresh() {
    if (isRequest) return
    requestGirlList(1)
  }

  //加载更多
  fun loadMore() {
    if (isRequest) return
    requestGirlList(currentPage + 1)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部处理">
  private val girlList = MutableLiveData<ListUiState<MutableList<GankGirlBean>>>()
  private var isRequest = false
  private var currentPage = 1
  private var pageSize = 10
  private var hasMore = true
  private fun requestGirlList(page: Int) {
    rxLifeScope.launch({
      //协程代码块
      val result = GankRepository.instance.girlList(page = page, size = pageSize)
      currentPage = page
      hasMore = result.size == pageSize
      //可以直接更新UI
      girlList.value = ListUiState(
        suc = true,
        hasMore = hasMore,
        data = if (page == 1) result else ((girlList.value?.data ?: mutableListOf()) + result).toMutableList()
      )
    }, { e -> //异常回调，这里可以拿到Throwable对象
      girlList.value = ListUiState(
        exc = e,
        hasMore = hasMore,
        data = girlList.value?.data ?: mutableListOf()
      )
    }, { //开始回调，可以开启等待弹窗
      isRequest = true
      girlList.value = ListUiState(
        isLoading = true,
        hasMore = hasMore,
        data = girlList.value?.data ?: mutableListOf()
      )
    }, { //结束回调，可以销毁等待弹窗
      isRequest = false
    })
  }
  //</editor-fold>
}