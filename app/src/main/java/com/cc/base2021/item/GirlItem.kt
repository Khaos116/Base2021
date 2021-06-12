package com.cc.base2021.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.ext.loadImgVertical
import com.cc.ext.*
import kotlinx.android.synthetic.main.item_girl.view.itemGirlIv

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:41
 */
class GirlItem(
    private val onItemClick: ((item: GankGirlBean, position: Int) -> Unit)? = null
) : BaseItemView<GankGirlBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_girl
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: GankGirlBean) {
    if (onItemClick == null) {
      itemView.pressEffectDisable()
      itemView.setOnClickListener(null)
    } else {
      itemView.pressEffectAlpha(0.9f)
      itemView.click { onItemClick.invoke(item, holder.layoutPosition) }
    }
    itemView.itemGirlIv.loadImgVertical(item.images?.firstOrNull(), 853f / 1280)
  }
  //</editor-fold>
}