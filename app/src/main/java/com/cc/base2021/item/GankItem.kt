package com.cc.base2021.item
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankAndroidBean
import com.cc.ext.setNumberNo00
import kotlinx.android.synthetic.main.item_gank.view.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:39
 */
class GankItem : BaseItemView<GankAndroidBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_gank
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: GankAndroidBean) {
    itemView.itemGankTime.text = item.publishedAt
    itemView.itemGankTitle.text = item.title
    itemView.itemGankDes.text = item.desc
    itemView.itemGankSeeCounts.setNumberNo00(item.views.toDouble())
    itemView.itemGankStoreCounts.setNumberNo00(item.stars.toDouble())
    itemView.itemGankPraiseCounts.setNumberNo00(item.likeCounts.toDouble())
  }
  //</editor-fold>
}