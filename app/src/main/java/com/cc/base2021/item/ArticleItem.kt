package com.cc.base2021.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.TimeUtils
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.wan.ArticleBean
import com.cc.ext.*
import kotlinx.android.synthetic.main.item_article.view.*

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:05
 */
class ArticleItem(
    private val onItemClick: ((item: ArticleBean) -> Unit)? = null
) : BaseItemView<ArticleBean>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_article
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: ArticleBean) {
    itemView.itemArticleCapterName.text = item.chapterName
    itemView.itemArticleTime.text = TimeUtils.millis2String(item.publishTime)
    itemView.itemArticleTitle.text = item.title
    itemView.itemArticleDes.text = item.desc
    itemView.itemArticleTitle.visibleGone(!item.title.isNullOrBlank())
    itemView.itemArticleDes.visibleGone(!item.desc.isNullOrBlank())
    if (onItemClick == null) {
      itemView.pressEffectDisable()
      itemView.setOnClickListener(null)
    } else {
      itemView.pressEffectBgColor()
      itemView.click { onItemClick.invoke(item) }
    }
  }
  //</editor-fold>
}