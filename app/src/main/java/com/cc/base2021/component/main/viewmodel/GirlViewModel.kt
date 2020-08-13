package com.cc.base2021.component.main.viewmodel

import androidx.lifecycle.*
import com.cc.base.viewmodel.BaseViewModel
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.rxhttp.repository.GankRepository
import rxhttp.wrapper.exception.ParseException

/**
 * https://juejin.im/post/6844904100090347528#heading-1
 * Author:case
 * Date:2020/8/13
 * Time:9:48
 */
class GirlViewModel : BaseViewModel() {
  //<editor-fold defaultstate="collapsed" desc="外部访问">
  val girlState: LiveData<MutableList<GankGirlBean>>
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
  private val girlList = MutableLiveData<MutableList<GankGirlBean>>()
  private var isRequest = false
  private var currentPage = 1
  private fun requestGirlList(page: Int) {
    rxLifeScope.launch({
      //协程代码块
      val result = GankRepository.instance.girlList(page = page)
      //可以直接更新UI
      if (page == 1) {
        girlList.value = result
      } else {
        girlList.value = (girlList.value ?: mutableListOf<GankGirlBean>() + result).toMutableList()
      }
      currentPage = page
    }, { e -> //异常回调，这里可以拿到Throwable对象
      uiListState.value = ListUiState(errorMsg = e.message, exc = e)
    }, { //开始回调，可以开启等待弹窗
      isRequest = true
      uiListState.value = ListUiState(isLoading = true)
    }, { //结束回调，可以销毁等待弹窗
      isRequest = false
      uiListState.value = ListUiState(isLoading = false)
    })
  }
  //</editor-fold>
}