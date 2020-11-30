package com.cc.base2021.comm

import com.cc.base.viewmodel.BaseViewModel
import com.cc.base.viewmodel.DataState
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.android.synthetic.main.fragment_girl.girlRefreshLayout

/**
 * Author:CASE
 * Date:2020-11-30
 * Time:16:17
 */
abstract class CommViewModel : BaseViewModel() {
  //处理刷新状态
  fun handleRefresh(refreshLayout: SmartRefreshLayout?, dataState: DataState<Any>?) {
    when (dataState) {
      is DataState.SuccessRefresh -> { //刷新成功，如果有数据则可以拉出"加载更多"或者"没有更多"
        refreshLayout?.setEnableRefresh(true) //允许下拉刷新(空数据重新刷新)
        val data = dataState.data
        if (data is List<*>) refreshLayout?.setEnableLoadMore(!data.isNullOrEmpty()) //列表数据不为空才能上拉
        else refreshLayout?.setEnableLoadMore(false) //非列表数据不能上拉
      }
      is DataState.SuccessMore -> refreshLayout?.finishLoadMore() //加载更多成功
      is DataState.FailMore -> refreshLayout?.finishLoadMore(false) //加载更多失败
      is DataState.Complete -> { //请求完成
        refreshLayout?.finishRefresh() //结束刷新(不论成功还是失败)
        refreshLayout?.setNoMoreData(!dataState.hasMore) //判断是否还有更多
      }
      else -> {
      }
    }
  }
}