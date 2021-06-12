package com.cc.base2021.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.local.DividerBean

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:35
 */
class DividerItem : BaseItemView<DividerBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_divider
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: RecyclerView.ViewHolder, itemView: View, item: DividerBean) {
    itemView.layoutParams.height = item.heightPx
    itemView.setBackgroundColor(item.bgColor)
  }
  //</editor-fold>
}