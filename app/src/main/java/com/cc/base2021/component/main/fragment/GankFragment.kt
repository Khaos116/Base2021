package com.cc.base2021.component.main.fragment

import android.graphics.Color
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.blankj.utilcode.util.StringUtils
import com.cc.base.ext.stopInertiaRolling
import com.cc.base.ext.toast
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.bean.local.*
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GankViewModel
import com.cc.base2021.component.web.WebActivity
import com.cc.base2021.item.*
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.fragment_gank.androidRecycler

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GankFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): GankFragment {
      val fragment = GankFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { GankViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    //下拉刷新(translateMode和下面SlidingConsumer对应)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(androidRecycler, false)
    mSmartSwipeRefresh?.swipeConsumer?.let {
      if (it is SlidingConsumer) { //https://qibilly.com/SmartSwipe-tutorial/pages/SmartSwipeRefresh.html
        it.setOverSwipeFactor(0f) //超过最大拖动距离的比例，0不允许超出
        it.relativeMoveFactor = 1f //视差效果(0-1)，1没有视差
      }
    }
    mSmartSwipeRefresh?.disableRefresh()
    mSmartSwipeRefresh?.disableLoadMore()
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
    androidRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    androidRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(LoadingItemViewBinder())
    multiTypeAdapter.register(DividerItemViewBinder())
    multiTypeAdapter.register(EmptyErrorItemViewBinder() { mViewModel.refresh() })
    multiTypeAdapter.register(GankItemViewBinder(onItemClick))
    multiTypeAdapter.register(NineGridViewBinder(onItemImgClick))
    //监听加载结果
    mViewModel.androidState.observe(this, Observer { list ->
      //处理下拉和上拉
      if (list.suc || list.exc != null) {
        mSmartSwipeRefresh?.finished(list.suc)
        mSmartSwipeRefresh?.swipeConsumer?.enableTop()
        mSmartSwipeRefresh?.isNoMoreData = !list.hasMore
        if (list.hasMore) mSmartSwipeRefresh?.swipeConsumer?.enableBottom()
      } else if (!list.isLoading) {
        return@Observer
      }
      //停止惯性滚动
      if (!multiTypeAdapter.items.isNullOrEmpty()) androidRecycler.stopInertiaRolling()
      //正常数据处理
      val items = ArrayList<Any>()
      list.data?.forEachIndexed { index, androidBean ->
        items.add(androidBean) //文章
        if (androidBean.imagesNoNull().isNotEmpty()) items.add(androidBean.imagesNoNull()) //图片
        if (index < (list.data?.size ?: 0) - 1) items.add(DividerBean(heightPx = 1, bgColor = Color.RED)) //分割线
      }
      //如果没有，判断是否要显示异常布局
      if (items.isEmpty()) {
        when {
          list.isLoading -> items.add(LoadingBean()) //加载中
          list.suc -> items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          list.exc != null -> items.add(EmptyErrorBean()) //如果是请求异常没有数据
        }
      }
      multiTypeAdapter.items = items
      multiTypeAdapter.notifyDataSetChanged()
    })
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    androidRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) androidRecycler.scrollToPosition(5)
      androidRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="点击事件">
  //文章item点击事件
  private var onItemClick: ((bean: GankAndroidBean, position: Int) -> Unit)? = { bean, _ ->
    bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
  }

  //九宫图片点击事件
  private var onItemImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? =
    { url, position, iv, list ->
      "position=${position},url=${url}".toast()
    }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    onItemClick = null
    onItemImgClick = null
    super.onDestroy()
  }
  //</editor-fold>
}