package com.cc.base.viewmodel

import androidx.lifecycle.ViewModel

/**
 * Author:case
 * Date:2020/8/13
 * Time:10:08
 */
open class BaseViewModel : ViewModel() {

  open class SimpleUiState<T>(
      val isLoading: Boolean = false,
      val suc: Boolean = false,
      var exc: Throwable? = null,
      val data: T? = null
  )

  open class ListUiState<T>(
      val isLoading: Boolean = false,
      val suc: Boolean = false,
      var exc: Throwable? = null,
      val hasMore: Boolean = true,
      val data: T? = null
  )

  /*
   * https://www.cnblogs.com/Jetictors/p/8157969.html
   * 密封类 用来表示受限的类继承结构
   */
  sealed class DataState<T>(val data: T? = null) {
    //请求开始
    class Start<T>(oldData: T?) : DataState<T>(data = oldData) {}

    //请求结束
    class Complete<T>(totalData: T?, val hasMore: Boolean) : DataState<T>(data = totalData) {}

    //刷新成功
    class SuccessRefresh<T>(newData: T?) : DataState<T>(data = newData) {}

    //加载更多成功
    class SuccessMore<T>(val newData: T?, totalData: T?) : DataState<T>(data = totalData) {}

    //刷新失败
    class FailRefresh<T>(oldData: T?, val exc: Throwable?) : DataState<T>(data = oldData) {}

    //加载更多失败
    class FailMore<T>(oldData: T?, val exc: Throwable?) : DataState<T>(data = oldData) {}

    //判断是否数据可能改变
    fun dataMaybeChange() = (this is Start || this is SuccessRefresh || this is SuccessMore || this is FailRefresh)
  }
}