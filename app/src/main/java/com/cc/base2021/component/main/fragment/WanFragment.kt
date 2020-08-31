package com.cc.base2021.component.main.fragment

import android.graphics.Color
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.billy.android.swipe.SmartSwipeRefresh
import com.billy.android.swipe.SmartSwipeRefresh.SmartSwipeRefreshDataLoader
import com.billy.android.swipe.consumer.SlidingConsumer
import com.cc.base.ext.stopInertiaRolling
import com.cc.base2021.R
import com.cc.base2021.bean.local.*
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.WanViewModel
import com.cc.base2021.component.web.WebActivity
import com.cc.base2021.item.*
import com.cc.base2021.widget.picsel.ImageEngine
import com.cc.base2021.widget.sticky.StickyAnyAdapter
import com.cc.base2021.widget.sticky.StickyControl
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_wan.wanRecycler

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class WanFragment private constructor() : CommFragment() {

  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): WanFragment {
      val fragment = WanFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_wan
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求和监听
  private val mViewModel: WanViewModel by lazy { WanViewModel() }

  //多类型适配器
  private val stickyAdapter = object : StickyAnyAdapter() {
    override fun isHeader(position: Int): Boolean {
      return position == 2
    }
  }

  //下拉刷新
  private var mSmartSwipeRefresh: SmartSwipeRefresh? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override
  fun lazyInit() {
    //下拉刷新(translateMode和下面SlidingConsumer对应)
    mSmartSwipeRefresh = SmartSwipeRefresh.translateMode(wanRecycler, false)
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
    wanRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    wanRecycler.adapter = stickyAdapter
    //注册多类型
    stickyAdapter.register(LoadingItemViewBinder())
    stickyAdapter.register(DividerItemViewBinder())
    //stickyAdapter.register(EmptyErrorItemViewBinder() { mViewModel.refresh() })
    stickyAdapter.register(BannerViewBinder(onItemBannerClick))
    stickyAdapter.register(ArticleViewBinder(onItemArticleClick))
    //实现Sticky悬浮效果
    StickyControl.any().adapter(stickyAdapter).setRecyclerView(wanRecycler).togo()
    //监听加载结果
    mViewModel.articleState.observe(this, Observer { list ->
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
      if (!stickyAdapter.items.isNullOrEmpty()) wanRecycler.stopInertiaRolling()
      val items = ArrayList<Any>()
      mViewModel.bannerState.value?.let { if (!it.isNullOrEmpty()) items.add(it) }
      if (items.isNotEmpty()) items.add(DividerBean(heightPx = 1, bgColor = Color.BLUE)) //分割线
      list?.data?.forEach { articleBean -> items.add(articleBean) }
      //如果没有，判断是否要显示异常布局
      if (items.isEmpty()) {
        when {
          list.isLoading -> items.add(LoadingBean()) //加载中
          //list.suc -> items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          list.exc != null -> items.add(EmptyErrorBean()) //如果是请求异常没有数据
        }
      }
      stickyAdapter.items = items
      stickyAdapter.notifyDataSetChanged()
    })
    mViewModel.bannerState.observe(this, Observer { list ->
      if (list.isNullOrEmpty()) return@Observer
      if (stickyAdapter.items.any { it is LoadingBean }) stickyAdapter.items = mutableListOf(list)
      else stickyAdapter.items = stickyAdapter.items.toMutableList().apply { add(0, list) }
      stickyAdapter.notifyDataSetChanged()
    })
    //请求数据
    mViewModel.refresh()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    wanRecycler.layoutManager?.let { manager ->
      val firstPosition = (manager as LinearLayoutManager).findFirstVisibleItemPosition()
      if (firstPosition > 5) wanRecycler.scrollToPosition(5)
      wanRecycler.smoothScrollToPosition(0)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="点击事件">
  //Banner item点击事件
  private var onItemBannerClick: ((bean: BannerBean, position: Int) -> Unit)? = { bean, _ ->
    bean.url?.let { u -> WebActivity.startActivity(mActivity, u) }
  }

  //文章item点击事件
  private var onItemArticleClick: ((bean: ArticleBean, position: Int) -> Unit)? = { bean, _ ->
    bean.link?.let { u -> WebActivity.startActivity(mActivity, u) }
  }

  //九宫图片点击事件
  private var onItemImgClick: ((url: String, p: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = { _, p, _, list ->
    val tempList = mutableListOf<LocalMedia>()
    list.forEach { s -> tempList.add(LocalMedia().also { it.path = s }) }
    //开始预览
    PictureSelector.create(this)
      .themeStyle(R.style.picture_default_style)
      .isNotPreviewDownload(true)
      .imageEngine(ImageEngine())
      .openExternalPreview(p, tempList)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onDestroy() {
    onItemBannerClick = null
    onItemArticleClick = null
    onItemImgClick = null
    super.onDestroy()
  }
  //</editor-fold>
}