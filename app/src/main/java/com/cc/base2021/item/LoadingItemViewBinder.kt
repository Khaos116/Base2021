package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base2021.R
import com.cc.base2021.bean.local.LoadingBean
import com.cc.base2021.item.LoadingItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_loading.view.itemLoadingTv

/**
 * Author:case
 * Date:2020/8/21
 * Time:20:40
 */
class LoadingItemViewBinder : ItemViewBinder<LoadingBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_loading, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: LoadingBean) {
    if (!item.msg.isNullOrBlank()) holder.itemView.itemLoadingTv.text = item.msg
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}