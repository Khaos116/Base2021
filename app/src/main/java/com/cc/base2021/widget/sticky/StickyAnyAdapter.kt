package com.cc.base2021.widget.sticky

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.drakeet.multitype.*

/**
 * Author:case
 * Date:2020/8/31
 * Time:16:00
 */
abstract class StickyAnyAdapter(
  var list: List<Any> = emptyList(),
  val apacity: Int = 0,
  var type: Types = MutableTypes(apacity)
) : MultiTypeAdapter(list, apacity, type), StickyHeaderAdapter {
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    super.onBindViewHolder(holder, position)
    holder.itemView.setBackgroundColor(if (isHeader(position)) Color.WHITE else Color.TRANSPARENT)
  }
}