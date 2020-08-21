package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base.ext.*
import com.cc.base2021.R
import com.cc.base2021.ext.loadGank
import com.cc.base2021.ext.loadImg
import com.cc.base2021.item.NineImgViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_nine_img.view.itemNineImgTv
import java.net.URLConnection

/**
 * Author:case
 * Date:2020/8/21
 * Time:14:55
 */
class NineImgViewBinder(
  private val onItemClick: ((item: String, position: Int) -> Unit)? = null
) : ItemViewBinder<String, ViewHolder>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_nine_img, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: String) {
    if (item.contains("gank.io")) {
      holder.itemView.itemNineImgTv.loadGank(item)
    } else {
      holder.itemView.itemNineImgTv.loadImg(item)
    }
    if (onItemClick != null) {
      holder.itemView.pressEffectAlpha(0.95f)
      holder.itemView.click { onItemClick.invoke(item, holder.layoutPosition) }
    } else {
      holder.itemView.setOnClickListener(null)
      holder.itemView.pressEffectDisable()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}