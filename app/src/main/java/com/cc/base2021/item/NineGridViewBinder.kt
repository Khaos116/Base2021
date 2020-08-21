package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.cc.base2021.R
import com.cc.base2021.item.NineGridViewBinder.ViewHolder
import com.cc.base2021.widget.decoration.GridSpaceItemDecoration
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_nine_grid.view.itemNineGridRecycler

/**
 * Author:case
 * Date:2020/8/21
 * Time:14:51
 */
class NineGridViewBinder(
  private val onItemImgClick: ((bean: String, position: Int) -> Unit)? = null
) : ItemViewBinder<List<String>, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="变量">
  //Item间距
  private val spaceItem = SizeUtils.dp2px(5f)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_nine_grid, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: List<String>) {
    val recyclerView = holder.itemView.itemNineGridRecycler
    val count = if (item.size == 1) 1 else if (item.size == 2 || item.size == 4) 2 else 3
    recyclerView.layoutManager = GridLayoutManager(holder.itemView.context, count)
    if (recyclerView.itemDecorationCount == 0) recyclerView.addItemDecoration(GridSpaceItemDecoration(spaceItem))
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(NineImgViewBinder(onItemImgClick))
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = item
    multiTypeAdapter.notifyDataSetChanged()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}