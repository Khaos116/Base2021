package com.cc.base2021.item

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.SizeUtils
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.local.GridImageBean
import com.cc.base2021.widget.drag.GridItemTouchHelperCallback
import com.cc.decoration.GridSpaceItemDecoration
import com.cc.ext.click2Parent
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_nine_grid.view.itemNineGridRecycler

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:46
 */
class NineGridItem(
    private var parentView: View? = null,
    private val onItemImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
) : BaseItemView<GridImageBean>() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //Item间距
  private val spaceItem = SizeUtils.dp2px(6f)

  //拖拽效果
  private var helper: ItemTouchHelper? = null
  private var helperCallback: GridItemTouchHelperCallback? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_nine_grid
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun fillData(holder: ViewHolder, itemView: View, item: GridImageBean) {
    val recyclerView = itemView.itemNineGridRecycler
    val urlSize = item.list.size
    val canDrag = urlSize != 5 && urlSize != 7 && urlSize != 8
    //拖动和外部点击不能兼容，所以只能适配一个
    if (!canDrag) recyclerView.click2Parent(parentView)
    val list = item.list
    val count = if (list.size == 1) 1 else if (list.size == 2 || list.size == 4) 2 else 3
    recyclerView.layoutManager = GridLayoutManager(itemView.context, count)
    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
    recyclerView.addItemDecoration(GridSpaceItemDecoration(spaceItem).setDragGridEdge(false))
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(NineImgItem { url, position, iv -> onItemImgClick?.invoke(url, position, iv, list) })
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = list
    multiTypeAdapter.notifyDataSetChanged()
    helper = null
    helperCallback = null
    if (canDrag) {
      //拖拽开始---->>>先置空，防止复用的时候一样的RecyclerView导致不执行attachToRecyclerView
      ItemTouchHelper(GridItemTouchHelperCallback(multiTypeAdapter).also { call -> helperCallback = call })
          .apply { attachToRecyclerView(recyclerView) }
          .let { helper = it }
      //拖拽结束---<<<
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="移出去后解除拖拽">
  override fun onViewDetachedFromWindow(holder: ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    helper?.attachToRecyclerView(null)
    helper = null
    helperCallback = null
  }
  //</editor-fold>
}