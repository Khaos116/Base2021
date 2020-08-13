package com.cc.base2021.item

import android.graphics.Color
import android.view.View
import com.blankj.utilcode.util.SizeUtils
import com.cc.base2021.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * Author:case
 * Date:2020/8/13
 * Time:15:38
 */
open class DividerItem(
  val heightPx: Int = SizeUtils.dp2px(1f),
  val bgColor: Int = Color.parseColor("#ebebeb")
) : AbstractItem<DividerItem.ViewHolder>() {
  override val layoutRes = R.layout.item_divider

  override val type: Int = this.javaClass.name.hashCode()

  override fun getViewHolder(v: View) = ViewHolder(v)

  class ViewHolder(view: View) : FastAdapter.ViewHolder<DividerItem>(view) {
    override fun bindView(item: DividerItem, payloads: List<Any>) {
      itemView.layoutParams.height = item.heightPx
      itemView.setBackgroundColor(item.bgColor)
    }

    override fun unbindView(item: DividerItem) {
    }
  }
}