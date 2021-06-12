package com.cc.base2021.widget.sticky

import android.graphics.Color
import android.view.View
import com.cc.ext.pressEffectBgColor
import com.cc.ext.pressEffectDisable
import com.cc.sticky.StickyHeaderCallbacks
import com.drakeet.multitype.*

/**
 * Author:Khaos
 * Date:2020/8/31
 * Time:16:00
 */
abstract class StickyAnyAdapter(
  var list: List<Any> = emptyList(),
  val apacity: Int = 0,
  var type: Types = MutableTypes(apacity)
) : MultiTypeAdapter(list, apacity, type), StickyHeaderCallbacks {
  override fun setupStickyHeaderView(stickyHeader: View) {
    super.setupStickyHeaderView(stickyHeader)
    stickyHeader.setBackgroundColor(Color.WHITE)
    stickyHeader.pressEffectBgColor()
  }

  override fun teardownStickyHeaderView(stickyHeader: View) {
    super.teardownStickyHeaderView(stickyHeader)
    stickyHeader.setBackgroundColor(Color.TRANSPARENT)
    stickyHeader.pressEffectDisable()
  }
}