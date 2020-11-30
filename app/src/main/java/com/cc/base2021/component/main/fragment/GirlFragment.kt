package com.cc.base2021.component.main.fragment

import android.graphics.Color
import androidx.recyclerview.widget.LinearLayoutManager
import com.cc.base.viewmodel.DataState
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.bean.local.*
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.main.viewmodel.GirlViewModel
import com.cc.base2021.item.*
import com.cc.base2021.widget.picsel.ImageEngine
import com.drakeet.multitype.MultiTypeAdapter
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.fragment_girl.girlRecycler
import kotlinx.android.synthetic.main.fragment_girl.girlRefreshLayout

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:48
 */
class GirlFragment private constructor() : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): GirlFragment {
      val fragment = GirlFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_girl

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //网络请求
  private val mViewModel by lazy { GirlViewModel() }

  //多类型适配器
  private val multiTypeAdapter = MultiTypeAdapter()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    girlRefreshLayout.setOnRefreshListener { mViewModel.refresh() }
    girlRefreshLayout.setOnLoadMoreListener { mViewModel.loadMore() }
    //设置适配器
    girlRecycler.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    girlRecycler.adapter = multiTypeAdapter
    //注册多类型
    multiTypeAdapter.register(LoadingItem())
    multiTypeAdapter.register(DividerItem())
    multiTypeAdapter.register(EmptyErrorItem() { mViewModel.refresh() })
    multiTypeAdapter.register(GirlItem() { item, _ ->
      //获取到图片列表(取对应类型且第一张图片地址不为空的数据)
      multiTypeAdapter.items.filterIsInstance<GankGirlBean>().mapNotNull { it.images?.firstOrNull() }.let { list ->
        val tempList = mutableListOf<LocalMedia>()
        val position = if (item.images?.firstOrNull().isNullOrBlank()) 0 else list.indexOf(item.images?.firstOrNull() ?: "")
        list.forEach { s -> tempList.add(LocalMedia().also { it.path = s }) }
        //开始预览
        PictureSelector.create(this)
            .themeStyle(R.style.picture_default_style)
            .isNotPreviewDownload(true)
            .imageEngine(ImageEngine())
            .openExternalPreview(position, tempList)
      }
    })
    //监听加载结果
    mViewModel.girlLiveData.observe(this) {
      mViewModel.handleRefresh(girlRefreshLayout, it)
      //正常数据处理
      var items = mutableListOf<Any>()
      when (it) {
        //开始请求
        is DataState.Start -> {
          if (it.data.isNullOrEmpty()) items.add(LoadingBean()) //加载中
          else items = multiTypeAdapter.items.toMutableList()
        }
        //刷新成功
        is DataState.SuccessRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean(isEmpty = true, isError = false)) //如果请求成功没有数据
          else it.data?.forEachIndexed { index, gankGirlBean ->
            items.add(gankGirlBean)
            if (index < (it.data?.size ?: 0) - 1) items.add(DividerBean(heightPx = 1, bgColor = Color.RED))
          }
        }
        //加载更多成功
        is DataState.SuccessMore -> {
          items = multiTypeAdapter.items.toMutableList()
          it.newData?.forEach { gankGirlBean ->
            items.add(DividerBean(heightPx = 1, bgColor = Color.RED))
            items.add(gankGirlBean)
          }
        }
        //刷新失败
        is DataState.FailRefresh -> {
          if (it.data.isNullOrEmpty()) items.add(EmptyErrorBean()) //如果是请求异常没有数据
          else items = multiTypeAdapter.items.toMutableList()
        }
        else -> {
        }
      }
      if (it?.dataMaybeChange() == true) {
        multiTypeAdapter.items = items
        multiTypeAdapter.notifyDataSetChanged()
      }
    }
    //请求数据
    mViewModel.refresh()
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