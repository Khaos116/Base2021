package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base2021.R
import com.cc.base2021.bean.local.DividerBean
import com.cc.base2021.item.DividerItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder

/**
 * Author:case
 * Date:2020/8/13
 * Time:22:20
 */
class DividerItemViewBinder : ItemViewBinder<DividerBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_divider, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: DividerBean) {
    holder.itemView.layoutParams.height = item.heightPx
    holder.itemView.setBackgroundColor(item.bgColor)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}