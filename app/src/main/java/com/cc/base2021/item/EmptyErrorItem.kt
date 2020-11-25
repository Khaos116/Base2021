package com.cc.base2021.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.local.EmptyErrorBean
import com.cc.ext.*
import com.cc.utils.NetUtils
import kotlinx.android.synthetic.main.item_empty_error.view.itemEmptyErrorTv

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:37
 */
class EmptyErrorItem(
    private val callRetry: (() -> Unit)? = null
) : BaseItemView<EmptyErrorBean>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_empty_error
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: RecyclerView.ViewHolder, itemView: View, item: EmptyErrorBean) {
    itemView.itemEmptyErrorTv.text = if (!item.msg.isNullOrBlank()) {
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
      itemView.itemEmptyErrorTv.pressEffectAlpha(0.9f)
      itemView.itemEmptyErrorTv.click {
        if (NetUtils.checkNetToast()) callRetry.invoke()
      }
    } else {
      itemView.itemEmptyErrorTv.pressEffectDisable()
      itemView.itemEmptyErrorTv.setOnClickListener(null)
    }
  }
  //</editor-fold>
}