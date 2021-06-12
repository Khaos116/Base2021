package com.cc.base2021.item

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.ext.loadImgSquare
import com.cc.ext.*
import kotlinx.android.synthetic.main.item_nine_img.view.itemNineImgTv

/**
 * Author:Khaos
 * Date:2020-11-25
 * Time:15:44
 */
class NineImgItem(
    private val onItemClick: ((item: String, position: Int, iv: ImageView) -> Unit)? = null
) : BaseItemView<String>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_nine_img
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: String) {
    holder.itemView.itemNineImgTv.loadImgSquare(item)
    if (onItemClick != null) {
      itemView.pressEffectAlpha(0.9f)
      itemView.click { onItemClick.invoke(item, holder.layoutPosition, holder.itemView.itemNineImgTv) }
    } else {
      itemView.setOnClickListener(null)
      itemView.pressEffectDisable()
    }
  }
  //</editor-fold>
}