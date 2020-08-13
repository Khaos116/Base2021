package com.cc.base2021.component.main.fragment

import androidx.lifecycle.Observer
import com.cc.base.ext.logE
import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GirlViewModel

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GirlFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  private val mViewModel by lazy { GirlViewModel() }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_girl

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  //初始化View
  override fun lazyInitView() {
    //监听加载状态
    mViewModel.uiListState.observe(this, Observer { state ->
      //加载中和加载结束
      if (state.isLoading) {
        showLoadingView()
      } else {
        dismissLoadingView()
      }
      //是否有更多
      state.hasMore
      //加载失败的处理
      state.errorMsg?.let { msg ->
        if (mViewModel.girlState.value.isNullOrEmpty()) showErrorView(msg) { mViewModel.refresh() }
      }
    })
    //监听加载成功
    mViewModel.girlState.observe(this, Observer { list ->
      for (bean in list) {
        bean.images?.firstOrNull()?.logE()
      }
    })
  }

  //初始数据
  override fun lazyInitDta() {
    mViewModel.refresh()
  }
  //</editor-fold>
}