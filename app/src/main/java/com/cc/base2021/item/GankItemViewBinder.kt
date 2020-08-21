package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base.ext.click
import com.cc.base.ext.setNumberNo00
import com.cc.base2021.R.layout
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.item.GankItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_gank.view.*

/**
 * Author:case
 * Date:2020/8/21
 * Time:10:23
 */
class GankItemViewBinder(val onItemClick: ((item: GankAndroidBean) -> Unit)? = null) :
  ItemViewBinder<GankAndroidBean, ViewHolder>() {

  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(layout.item_gank, parent, false)
    return ViewHolder(root)
  }

  override fun onBindViewHolder(holder: ViewHolder, item: GankAndroidBean) {
    holder.itemView.itemGankTime.text = item.publishedAt
    holder.itemView.itemGankTitle.text = item.title
    holder.itemView.itemGankDes.text = item.desc
    holder.itemView.itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    holder.itemView.itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    holder.itemView.itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
    if (onItemClick != null) {
      holder.itemView.click { onItemClick?.invoke(item) }
    } else {
      holder.itemView.setOnClickListener(null)
    }
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}