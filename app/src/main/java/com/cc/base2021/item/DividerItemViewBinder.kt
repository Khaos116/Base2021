package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base2021.R.layout
import com.cc.base2021.bean.local.DividerBean
import com.cc.base2021.item.DividerItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder

/**
 * Author:case
 * Date:2020/8/13
 * Time:22:20
 */
class DividerItemViewBinder : ItemViewBinder<DividerBean, ViewHolder>() {

  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(layout.item_divider, parent, false)
    return ViewHolder(root)
  }

  override fun onBindViewHolder(holder: ViewHolder, item: DividerBean) {
    holder.itemView.layoutParams.height = item.heightPx
    holder.itemView.setBackgroundColor(item.bgColor)
  }

  class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)
}