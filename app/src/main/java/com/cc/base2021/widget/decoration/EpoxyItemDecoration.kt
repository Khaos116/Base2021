package com.cc.base2021.widget.decoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Px
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.RecyclerView.State

/**
 * 没有四周间距的ItemDecorator
 * 源码：https://github.com/airbnb/epoxy/blob/master/epoxy-adapter/src/main/java/com/airbnb/epoxy/EpoxyItemSpacingDecorator.java
 * Modifies item spacing in a recycler view so that items are equally spaced no matter where they
 * are on the grid. Only designed to work with standard linear or grid layout managers.
 */
class EpoxyItemDecoration @JvmOverloads constructor(
  @Px private val pxBetweenItems: Int = 0,
  private val mIncludeStartEnd: Boolean = false,/*距屏幕左右是否有间距*/
  private val mIncludeTop: Boolean = false,/*开始的第一排顶部是否有间距*/
  private val mIncludeBottom: Boolean = false/*结束的最后一排底部是否也有间距*/
) : ItemDecoration() {
  private var verticallyScrolling = false
  private var horizontallyScrolling = false
  private var firstItem = false
  private var lastItem = false
  private var grid = false
  private var isFirstItemInRow = false
  private var fillsLastSpan = false
  private var isInFirstRow = false
  private var isInLastRow = false

  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
    // Zero everything out for the common case
    outRect.setEmpty()
    val position = parent.getChildAdapterPosition(view)
    if (position == RecyclerView.NO_POSITION) {
      // View is not shown
      return
    }
    val layout = parent.layoutManager
    calculatePositionDetails(parent, position, layout)
    var left = useLeftPadding()
    var right = useRightPadding()
    var top = useTopPadding()
    var bottom = useBottomPadding()
    if (shouldReverseLayout(layout, horizontallyScrolling)) {
      if (horizontallyScrolling) {
        val temp = left
        left = right
        right = temp
      } else {
        val temp = top
        top = bottom
        bottom = temp
      }
    }
    // Divided by two because it is applied to the left side of one item and the right of another
    // to add up to the total desired space
    val padding = pxBetweenItems / 2
    outRect.right = if (right) padding else 0
    outRect.left = if (left) padding else 0
    outRect.top = if (top) padding else 0
    outRect.bottom = if (bottom) padding else 0
    //===================添加代码START===================//
    if (mIncludeStartEnd && !right) outRect.right = pxBetweenItems
    if (mIncludeStartEnd && !left) outRect.left = pxBetweenItems
    if (mIncludeTop && !top) outRect.top = pxBetweenItems
    if (mIncludeBottom && !bottom) outRect.bottom = pxBetweenItems
    //===================添加代码END===================//
  }

  private fun calculatePositionDetails(parent: RecyclerView, position: Int, layout: LayoutManager?) {
    val itemCount = parent.adapter?.itemCount ?: 0
    firstItem = position == 0
    lastItem = position == itemCount - 1
    horizontallyScrolling = layout?.canScrollHorizontally() ?: false
    verticallyScrolling = layout?.canScrollVertically() ?: true
    grid = layout is GridLayoutManager
    if (grid) {
      val grid = layout as GridLayoutManager
      val spanSizeLookup = grid.spanSizeLookup
      val spanSize = spanSizeLookup.getSpanSize(position)
      val spanCount = grid.spanCount
      val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount)
      isFirstItemInRow = spanIndex == 0
      fillsLastSpan = spanIndex + spanSize == spanCount
      isInFirstRow = isInFirstRow(position, spanSizeLookup, spanCount)
      isInLastRow = !isInFirstRow && isInLastRow(position, itemCount, spanSizeLookup, spanCount)
    }
  }

  private fun useBottomPadding(): Boolean {
    return if (grid) (horizontallyScrolling && !fillsLastSpan || verticallyScrolling && !isInLastRow) else verticallyScrolling && !lastItem
  }

  private fun useTopPadding(): Boolean {
    return if (grid) (horizontallyScrolling && !isFirstItemInRow || verticallyScrolling && !isInFirstRow) else verticallyScrolling && !firstItem
  }

  private fun useRightPadding(): Boolean {
    return if (grid) (horizontallyScrolling && !isInLastRow || verticallyScrolling && !fillsLastSpan) else horizontallyScrolling && !lastItem
  }

  private fun useLeftPadding(): Boolean {
    return if (grid) (horizontallyScrolling && !isInFirstRow || verticallyScrolling && !isFirstItemInRow) else horizontallyScrolling && !firstItem
  }

  companion object {
    private fun shouldReverseLayout(layout: LayoutManager?, horizontallyScrolling: Boolean): Boolean {
      var reverseLayout = layout is LinearLayoutManager && layout.reverseLayout
      val rtl = layout?.layoutDirection == ViewCompat.LAYOUT_DIRECTION_RTL
      if (horizontallyScrolling && rtl) {
        // This is how linearlayout checks if it should reverse layout in #resolveShouldLayoutReverse
        reverseLayout = !reverseLayout
      }
      return reverseLayout
    }

    private fun isInFirstRow(position: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Boolean {
      var totalSpan = 0
      for (i in 0..position) {
        totalSpan += spanSizeLookup.getSpanSize(i)
        if (totalSpan > spanCount) {
          return false
        }
      }
      return true
    }

    private fun isInLastRow(position: Int, itemCount: Int, spanSizeLookup: SpanSizeLookup, spanCount: Int): Boolean {
      var totalSpan = 0
      for (i in itemCount - 1 downTo position) {
        totalSpan += spanSizeLookup.getSpanSize(i)
        if (totalSpan > spanCount) {
          return false
        }
      }
      return true
    }
  }
}