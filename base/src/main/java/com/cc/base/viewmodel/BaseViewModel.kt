package com.cc.base.viewmodel

import androidx.lifecycle.*

/**
 * Author:case
 * Date:2020/8/13
 * Time:10:08
 */
open class BaseViewModel : ViewModel() {
  val uiSimpleState = MutableLiveData<SimpleUiState>()
  val uiListState = MutableLiveData<ListUiState>()

  open class SimpleUiState(
    val isLoading: Boolean = false,
    val suc: Boolean = false,
    var exc: Throwable? = null
  )

  open class ListUiState(
    val isLoading: Boolean = false,
    val suc: Boolean = false,
    var exc: Throwable? = null,
    val hasMore: Boolean = true
  )
}