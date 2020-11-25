package com.cc.base.viewmodel

import androidx.lifecycle.*

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
}