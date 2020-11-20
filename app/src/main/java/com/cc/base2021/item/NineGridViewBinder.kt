package com.cc.base2021.item

import android.annotation.SuppressLint
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.blankj.utilcode.util.SizeUtils
import com.cc.base2021.R
import com.cc.base2021.bean.local.GridImageBean
import com.cc.base2021.item.NineGridViewBinder.ViewHolder
import com.cc.decoration.GridDragItemDecoration
import com.cc.drag.ItemTouchMoveListener
import com.cc.drag.MyItemTouchHelperCallback
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_nine_grid.view.itemNineGridRecycler
import java.util.Collections

/**
 * Author:case
 * Date:2020/8/21
 * Time:14:51
 */
class NineGridViewBinder(
    private val onItemImgClick: ((url: String, position: Int, iv: ImageView, list: MutableList<String>) -> Unit)? = null,
    private val onItemClick: ((url: String) -> Unit)? = null,
) : ItemViewBinder<GridImageBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="变量">
  //Item间距
  private val spaceItem = SizeUtils.dp2px(6f)

  //拖拽效果
  private var mapHelper: MutableMap<Int, ItemTouchHelper> = hashMapOf()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_nine_grid, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  @SuppressLint("ClickableViewAccessibility")
  override fun onBindViewHolder(holder: ViewHolder, item: GridImageBean) {
    val recyclerView = holder.itemView.itemNineGridRecycler
    recyclerView.setOnTouchListener { _, event -> holder.itemView.onTouchEvent(event) }
    val list = item.list
    val count = if (list.size == 1) 1 else if (list.size == 2 || list.size == 4) 2 else 3
    recyclerView.layoutManager = GridLayoutManager(holder.itemView.context, count)
    if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
    recyclerView.addItemDecoration(GridDragItemDecoration(spaceItem))
    val multiTypeAdapter = MultiTypeAdapter()
    multiTypeAdapter.register(NineImgViewBinder { url, position, iv ->
      onItemImgClick?.invoke(url, position, iv, list)
    })
    recyclerView.adapter = multiTypeAdapter
    multiTypeAdapter.items = list
    multiTypeAdapter.notifyDataSetChanged()
    //点击事件会导致拖拽无法触发
    //holder.itemView.click { onItemClick?.invoke(item.url) }
    //拖拽开始---->>>先置空，防止复用的时候一样的RecyclerView导致不执行attachToRecyclerView
    mapHelper[recyclerView.hashCode()]?.attachToRecyclerView(null)
    ItemTouchHelper(MyItemTouchHelperCallback(object : ItemTouchMoveListener {
      override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        //1.数据交换；2.刷新
        Collections.swap(list, fromPosition, toPosition)
        multiTypeAdapter.notifyItemMoved(fromPosition, toPosition)
        return true
      }

      override fun onItemRemove(position: Int): Boolean {
        //不需要侧滑删除
        //item.removeAt(position)
        //multiTypeAdapter.notifyItemRemoved(position)
        return false
      }
    }, false)).apply { attachToRecyclerView(recyclerView) }
        .let { mapHelper[recyclerView.hashCode()] = it }
    //拖拽结束---<<<
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}