package com.cc.base2021.component.main.fragment

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ColorUtils
import com.cc.base2021.R
import com.cc.base2021.bean.local.DividerBean
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GirlViewModel
import com.cc.base2021.item.*
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_girl.girlRecycler

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GirlFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { GirlViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()
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
    //注册多类型
    multiTypeAdapter.register(DividerItemViewBinder())
    multiTypeAdapter.register(GirlItemViewBinder())
    //设置适配器
    girlRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    girlRecycler.adapter = multiTypeAdapter
  }

  //初始数据
  override fun lazyInitDta() {
    //监听加载成功
    mViewModel.girlState.observe(this, Observer { list ->
      val items = ArrayList<Any>()
      list.forEachIndexed { index, gankGirlBean ->
        items.add(gankGirlBean)
        if (index < list.size - 1) items.add(DividerBean(heightPx = 1,bgColor = Color.RED))
      }
      multiTypeAdapter.items = items
      multiTypeAdapter.notifyDataSetChanged()
    })
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>
}