package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.cc.base.ext.*
import com.cc.base2021.R.layout
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.base2021.item.GankItemViewBinder.ViewHolder
import com.cc.base2021.widget.decoration.GridSpaceItemDecoration
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.item_gank.view.*

/**
 * Author:case
 * Date:2020/8/21
 * Time:10:23
 */
class GankItemViewBinder(
  private val onItemClick: ((item: GankAndroidBean, position: Int) -> Unit)? = null
) : ItemViewBinder<GankAndroidBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="变量">
  //Item间距
  private val spaceItem = SizeUtils.dp2px(5f)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="点击事件">
  private var onItemImgClick: ((bean: String, position: Int) -> Unit)? = { img, position ->
    position.toString().toast()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(layout.item_gank, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="填充数据">
  override fun onBindViewHolder(holder: ViewHolder, item: GankAndroidBean) {
    holder.itemView.itemGankTime.text = item.publishedAt
    holder.itemView.itemGankTitle.text = item.title
    holder.itemView.itemGankDes.text = item.desc
    holder.itemView.itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    holder.itemView.itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    holder.itemView.itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
    val sizeImg = item.imagesNoNull().size
    val rv = RecyclerView(holder.itemView.context)
    rv.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    holder.itemView.itemGankNineRecyclerParent.visibleGone(sizeImg > 0)
    if (sizeImg > 0) {
      holder.itemView.itemGankNineRecyclerParent.removeAllViews()
      holder.itemView.itemGankNineRecyclerParent.addView(rv, ViewGroup.LayoutParams(-1, -1))
      val count = if (sizeImg == 1) 1 else if (sizeImg == 2 || sizeImg == 4) 2 else 3
      rv.layoutManager = GridLayoutManager(holder.itemView.context, count)
      rv.addItemDecoration(GridSpaceItemDecoration(spaceItem))
      val multiTypeAdapter = MultiTypeAdapter()
      multiTypeAdapter.register(NineImgViewBinder())
      multiTypeAdapter.items = item.imagesNoNull()
      rv.adapter = multiTypeAdapter
    }
    if (onItemClick != null) {
      holder.itemView.pressEffectBgColor()
      holder.itemView.click { onItemClick.invoke(item, holder.layoutPosition) }
    } else {
      holder.itemView.setOnClickListener(null)
      holder.itemView.pressEffectDisable()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="默认ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}