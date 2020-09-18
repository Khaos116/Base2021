package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.ext.*
import com.cc.base.utils.NetUtils
import com.cc.base2021.R
import com.cc.base2021.bean.local.EmptyErrorBean
import com.cc.base2021.item.EmptyErrorItemViewBinder.ViewHolder
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_empty_error.view.itemEmptyErrorTv

/**
 * Author:case
 * Date:2020/8/21
 * Time:17:25
 */
class EmptyErrorItemViewBinder(
  private val callRetry: (() -> Unit)? = null
) : ItemViewBinder<EmptyErrorBean, ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_empty_error, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun onBindViewHolder(holder: ViewHolder, item: EmptyErrorBean) {
    holder.itemView.itemEmptyErrorTv.text = if (!item.msg.isNullOrBlank()) {
      item.msg
    } else if (!item.isError) {
      StringUtils.getString(R.string.page_empty)
    } else {
      if (NetworkUtils.isConnected()) {
        StringUtils.getString(R.string.page_fail_error)
      } else {
        StringUtils.getString(R.string.page_net_error)
      }
    }
    if (callRetry != null && item.isError) {
      holder.itemView.itemEmptyErrorTv.pressEffectAlpha(0.9f)
      holder.itemView.itemEmptyErrorTv.click {
        if (NetUtils.checkNetToast()) callRetry.invoke()
      }
    } else {
      holder.itemView.itemEmptyErrorTv.pressEffectDisable()
      holder.itemView.itemEmptyErrorTv.setOnClickListener(null)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}