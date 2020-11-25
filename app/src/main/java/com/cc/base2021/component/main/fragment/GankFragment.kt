package com.cc.base2021.component.main.fragment

import android.graphics.Color
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.cc.base2021.R
import com.cc.base2021.bean.local.*
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GankViewModel
import com.cc.base2021.component.web.WebActivity
import com.cc.base2021.item.*
import com.cc.base2021.widget.picsel.ImageEngine
import com.cc.ext.stopInertiaRolling
import com.drakeet.multitype.MultiTypeAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_gank.gankRecycler

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GankFragment private constructor() : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): GankFragment {
      val fragment = GankFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { GankViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    //下拉刷新(translateMode和下面SlidingConsumer对应)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(gankRecycler, false)
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
      override fun onRefresh(ssr: SmartSwipeRefresh?) = mViewModel.refresh()
      override fun onLoadMore(ssr: SmartSwipeRefresh?) = mViewModel.loadMore()
    }
    //设置适配器
    gankRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    gankRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(LoadingItem())
    multiTypeAdapter.register(DividerItem())
    multiTypeAdapter.register(EmptyErrorItem() { mViewModel.refresh() })
    multiTypeAdapter.register(GankItem() { bean ->
      bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
    })
    multiTypeAdapter.register(NineGridItem(onItemImgClick = { _, p, _, list ->
      val tempList = mutableListOf<LocalMedia>()
      list.forEach { s -> tempList.add(LocalMedia().also { it.path = s }) }
      //开始预览
      PictureSelector.create(this)
          .themeStyle(R.style.picture_default_style)
          .isNotPreviewDownload(true)
          .imageEngine(ImageEngine())
          .openExternalPreview(p, tempList)
    }, onItemClick = { url ->
      if (url.isNotBlank()) WebActivity.startActivity(mActivity, url)
    }))
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
      if (!multiTypeAdapter.items.isNullOrEmpty()) gankRecycler.stopInertiaRolling()
      //正常数据处理
      val items = ArrayList<Any>()
      list.data?.forEachIndexed { index, androidBean ->
        items.add(androidBean) //文章
        if (androidBean.imagesNoNull().isNotEmpty()) items.add(
            GridImageBean(androidBean.url ?: "", androidBean.imagesNoNull())) //图片
        if (index < (list.data?.size ?: 0) - 1) items.add(DividerBean(heightPx = 1, bgColor = Color.GREEN)) //分割线
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
    gankRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) gankRecycler.scrollToPosition(5)
      gankRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>
}