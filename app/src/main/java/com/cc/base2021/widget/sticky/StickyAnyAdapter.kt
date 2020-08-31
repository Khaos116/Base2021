package com.cc.base2021.widget.sticky

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
}