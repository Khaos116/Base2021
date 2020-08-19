package com.cc.base2021.component.main.fragment

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.billy.android.swipe.SmartSwipeRefresh
import com.cc.base2021.R
import com.cc.base2021.bean.local.DividerBean
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GirlViewModel
import com.cc.base2021.item.DividerItemViewBinder
import com.cc.base2021.item.GirlItemViewBinder
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

  //页面销毁时的位置
  private var lastPosition = 0

  //页面销毁时的偏移量
  private var lastPositionOff = 0

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_girl

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    //设置适配器
    girlRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    girlRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(DividerItemViewBinder())
    multiTypeAdapter.register(GirlItemViewBinder())
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
      val items = ArrayList<Any>()
      list.forEachIndexed { index, gankGirlBean ->
        items.add(gankGirlBean)
        if (index < list.size - 1) items.add(DividerBean(heightPx = 1, bgColor = Color.RED))
      }
      multiTypeAdapter.items = items
      multiTypeAdapter.notifyDataSetChanged()
    })
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroyView() {
    super.onDestroyView()
    (girlRecycler.layoutManager as LinearLayoutManager).let { manager ->
      lastPosition = manager.findFirstVisibleItemPosition()
      if (girlRecycler.childCount > 0) {
        lastPositionOff = girlRecycler.getChildAt(0).top
      }
    }

  }
  //</editor-fold>
}