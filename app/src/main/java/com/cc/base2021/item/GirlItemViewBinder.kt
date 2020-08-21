package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.cc.base2021.R.layout
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.ext.loadGank
import com.cc.base2021.item.GirlItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_girl.view.itemGirlIv

/**
 * Author:case
 * Date:2020/8/13
 * Time:22:25
 */
class GirlItemViewBinder : ItemViewBinder<GankGirlBean, ViewHolder>() {

  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(layout.item_girl, parent, false)
    return ViewHolder(root)
  }

  override fun onBindViewHolder(holder: ViewHolder, item: GankGirlBean) {
    holder.itemView.itemGirlIv.loadGank(item.images?.firstOrNull(), true)
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}