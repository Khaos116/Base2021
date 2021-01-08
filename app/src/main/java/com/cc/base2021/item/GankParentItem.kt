package com.cc.base2021.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.bean.local.GridImageBean
import com.cc.ext.click
import com.cc.ext.click2Parent
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_gank_parent.view.itemGankParentRecycler

/**
 * @Author:CASE
 * @Date:2021年1月8日
 * @Time:16:23:13
 */
class GankParentItem(
    private val onItemClick: ((item: GankAndroidBean) -> Unit)? = null,
    private val onImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
) : BaseItemView<GankAndroidBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_gank_parent
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun fillData(holder: ViewHolder, itemView: View, item: GankAndroidBean) {
    itemView.click { onItemClick?.invoke(item) }
    val items = mutableListOf<Any>()
    items.add(item)
    if (item.imagesNoNull().isNotEmpty()) items.add(GridImageBean(url = item.url ?: "", list = item.imagesNoNull()))
    val recyclerView = itemView.itemGankParentRecycler
    recyclerView.click2Parent(itemView)
    recyclerView.adapter = null
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(GankItem())
    multiTypeAdapter.register(NineGridItem(parentView = itemView) { url, p, iv, list -> onImgClick?.invoke(url, p, iv, list) })
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = items
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>
}