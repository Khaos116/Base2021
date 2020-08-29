package com.cc.base2021.component.guide.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cc.base.ext.*
import com.cc.base2021.R
import com.cc.base2021.component.main.MainActivity
import com.cc.base2021.ext.loadImgVerticalScreen
import com.cc.base2021.utils.MMkvUtils
import com.cc.base2021.widget.discretescrollview.holder.DiscreteHolder
import com.cc.base2021.widget.discretescrollview.holder.DiscreteHolderCreator
import kotlinx.android.synthetic.main.layout_guide.view.guideGo
import kotlinx.android.synthetic.main.layout_guide.view.guideIV

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/14 14:30
 */
class GuideHolderCreator : DiscreteHolderCreator {
  override fun createHolder(itemView: View): DiscreteHolder<String> = GuideHolder(itemView)

  override fun getLayoutId() = R.layout.layout_guide
}

class GuideHolder(view: View) : DiscreteHolder<String>(view) {
  private var imageView: ImageView? = null
  private var textView: TextView? = null
  override fun initView(itemView: View) {
    imageView = itemView.guideIV
    textView = itemView.guideGo
  }

  override fun updateUI(
    data: String?,
    position: Int,
    count: Int
  ) {
    this.imageView?.loadImgVerticalScreen(data)
    this.textView?.visibleGone(position == count - 1)
    this.textView?.let { view ->
      view.visibleGone(position == count - 1)
      view.pressEffectAlpha()
      view.click {
        MMkvUtils.instance.setShowGuide(false)
        MainActivity.startActivity(it.context)
      }
    }
  }
}