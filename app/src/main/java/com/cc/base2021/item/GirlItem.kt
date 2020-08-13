package com.cc.base2021.item

import android.view.View
import com.cc.base2021.R
import com.cc.base2021.bean.gank.GankGirlBean
import com.cc.base2021.ext.loadGank
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.synthetic.main.item_girl.view.itemGirlIv

/**
 * Author:case
 * Date:2020/8/13
 * Time:14:56
 */
open class GirlItem(var girl: GankGirlBean) : AbstractItem<GirlItem.ViewHolder>() {
  override val layoutRes = R.layout.item_girl

  override val type: Int = this.javaClass.name.hashCode()

  override fun getViewHolder(v: View) = ViewHolder(v)

  class ViewHolder(view: View) : FastAdapter.ViewHolder<GirlItem>(view) {
    private var imgIv = view.itemGirlIv

    override fun bindView(item: GirlItem, payloads: List<Any>) {
      imgIv.loadGank(item.girl.images?.firstOrNull())
    }

    override fun unbindView(item: GirlItem) {
      imgIv.setImageDrawable(null)
    }
  }
}