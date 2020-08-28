package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base.ext.*
import com.cc.base2021.R.layout
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.ext.loadFullScreen
import com.cc.base2021.item.GirlItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_girl.view.itemGirlIv

/**
 * Author:case
 * Date:2020/8/13
 * Time:22:25
 */
class GirlItemViewBinder(
  private val onItemClick: ((item: GankGirlBean, position: Int) -> Unit)? = null
) : ItemViewBinder<GankGirlBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(layout.item_girl, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: GankGirlBean) {
    if (onItemClick == null) {
      holder.itemView.pressEffectDisable()
      holder.itemView.setOnClickListener(null)
    } else {
      holder.itemView.pressEffectAlpha(0.95f)
      holder.itemView.click { onItemClick.invoke(item, holder.layoutPosition) }
    }
    holder.itemView.itemGirlIv.loadFullScreen(item.images?.firstOrNull())
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}