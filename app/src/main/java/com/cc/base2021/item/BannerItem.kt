package com.cc.base2021.item

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cc.base.ui.BaseItemView
import com.cc.base2021.R
import com.cc.base2021.bean.wan.BannerBean
import com.cc.base2021.ext.loadImgHorizontal
import com.cc.base2021.widget.discretescrollview.DSVOrientation
import com.cc.base2021.widget.discretescrollview.banner.DiscreteBanner
import com.cc.base2021.widget.discretescrollview.banner.holder.DiscreteHolder
import com.cc.base2021.widget.discretescrollview.banner.holder.DiscreteHolderCreator
import kotlinx.android.synthetic.main.item_banner_img.view.itemBannerImg

/**
 * Author:CASE
 * Date:2020-11-25
 * Time:15:49
 */
class BannerItem(
    private val onItemBannerClick: ((item: BannerBean, position: Int) -> Unit)? = null
) : BaseItemView<MutableList<BannerBean>>() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.item_banner
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据填充">
  override fun fillData(holder: ViewHolder, itemView: View, item: MutableList<BannerBean>) {
    if (holder.itemView.getTag(R.id.tag_banner) == item) return
    holder.itemView.setTag(R.id.tag_banner, item)
    holder.itemView.findViewById<DiscreteBanner<BannerBean>>(R.id.itemBanner)
        .setLooper(true)
        .setAutoPlay(true)
        .setOrientation(DSVOrientation.VERTICAL)
        .setOnItemClick { position, t -> onItemBannerClick?.invoke(t, position) }
        .apply {
          getIndicator()?.needSpecial = false
          if (getOrientation() == DSVOrientation.HORIZONTAL.ordinal) {
            setIndicatorGravity(Gravity.BOTTOM or Gravity.END)
            setIndicatorOffsetY(-defaultOffset / 2f)
            setIndicatorOffsetX(-defaultOffset)
          }
        }
        .setPages(object : DiscreteHolderCreator {
          override fun createHolder(itemView: View) = BannerHolderHolderView(itemView)
          override fun getLayoutId() = R.layout.item_banner_img
        }, item)
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Banner图片的ViewHolder">
  class BannerHolderHolderView(view: View) : DiscreteHolder<BannerBean>(view) {
    private var imageView: ImageView? = null

    override fun initView(itemView: View) {
      imageView = itemView.itemBannerImg
    }

    override fun updateUI(data: BannerBean, position: Int, count: Int) {
      this.imageView?.loadImgHorizontal(data.imagePath)
    }
  }
  //</editor-fold>
}