package com.cc.base2021.component.main.fragment

import android.graphics.Color
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.cc.base.ext.stopInertiaRolling
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
    //下拉刷新(translateMode和下面SlidingConsumer对应)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(girlRecycler, false)
    (girlRecycler.parent as View).setBackgroundColor(Color.parseColor("#f3f3f3"))
    mSmartSwipeRefresh?.swipeConsumer?.let {
      if (it is SlidingConsumer) { //https://qibilly.com/SmartSwipe-tutorial/pages/SmartSwipeRefresh.html
        it.setOverSwipeFactor(0f) //超过最大拖动距离的比例，0不允许超出
        it.relativeMoveFactor = 1f //视差效果(0-1)，1没有视差
      }
    }
    mSmartSwipeRefresh?.disableRefresh()
    mSmartSwipeRefresh?.isNoMoreData = true
    //下拉刷新
    mSmartSwipeRefresh?.dataLoader = object : SmartSwipeRefreshDataLoader {
      override fun onLoadMore(ssr: SmartSwipeRefresh?) {
        mViewModel.loadMore()
      }

      override fun onRefresh(ssr: SmartSwipeRefresh?) {
        mViewModel.refresh()
      }
    }
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
        if (multiTypeAdapter.items.isNullOrEmpty()) showLoadingView()
      } else {
        dismissLoadingView()
        if (!multiTypeAdapter.items.isNullOrEmpty()) mSmartSwipeRefresh?.swipeConsumer?.enableTop()
        //请求完成
        mSmartSwipeRefresh?.finished(state.suc)
        //是否有更多
        mSmartSwipeRefresh?.isNoMoreData = !state.hasMore
      }
      //加载失败的处理
      state.exc?.let { e ->
        if (mViewModel.girlState.value.isNullOrEmpty()) showErrorView(e.message) { mViewModel.refresh() }
      }
    })
    //监听加载成功
    mViewModel.girlState.observe(this, Observer { list ->
      //停止惯性滚动
      girlRecycler.stopInertiaRolling()
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

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    girlRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) girlRecycler.scrollToPosition(5)
      girlRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}