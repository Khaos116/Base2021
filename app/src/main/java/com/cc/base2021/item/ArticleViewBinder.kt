package com.cc.base2021.item

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.TimeUtils
import com.cc.base.ext.*
import com.cc.base2021.R
import com.cc.base2021.bean.wan.ArticleBean
import com.drakeet.multitype.ItemViewBinder
import kotlinx.android.synthetic.main.item_article.view.*

/**
 * Author:case
 * Date:2020/8/29
 * Time:18:41
 */
class ArticleViewBinder(
  private val onItemClick: ((item: ArticleBean, position: Int) -> Unit)? = null
) : ItemViewBinder<ArticleBean, ArticleViewBinder.ViewHolder>() {

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
    val root = inflater.inflate(R.layout.item_article, parent, false)
    return ViewHolder(root)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="加载数据">
  override fun onBindViewHolder(holder: ViewHolder, item: ArticleBean) {
    holder.itemView.itemArticleCapterName.text = item.chapterName
    holder.itemView.itemArticleTime.text = TimeUtils.millis2String(item.publishTime)
    holder.itemView.itemArticleTitle.text = item.title
    holder.itemView.itemArticleDes.text = item.desc
    holder.itemView.itemArticleTitle.visibleGone(!item.title.isNullOrBlank())
    holder.itemView.itemArticleDes.visibleGone(!item.desc.isNullOrBlank())
    if (onItemClick == null) {
      holder.itemView.pressEffectDisable()
      holder.itemView.setOnClickListener(null)
    } else {
      holder.itemView.pressEffectBgColor()
      holder.itemView.click { onItemClick.invoke(item, holder.layoutPosition) }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="ViewHolder">
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
  //</editor-fold>
}