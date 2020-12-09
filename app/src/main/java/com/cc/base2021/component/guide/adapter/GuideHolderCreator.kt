package com.cc.base2021.component.guide.adapter

import android.view.View
import com.blankj.utilcode.util.ScreenUtils
import com.cc.base2021.R
import com.cc.base2021.component.main.MainActivity
import com.cc.base2021.ext.loadImgVertical
import com.cc.base2021.utils.MMkvUtils
import com.cc.base2021.widget.discretescrollview.banner.holder.DiscreteHolder
import com.cc.base2021.widget.discretescrollview.banner.holder.DiscreteHolderCreator
import com.cc.ext.*
import kotlinx.android.synthetic.main.layout_guide.view.guideGo
import kotlinx.android.synthetic.main.layout_guide.view.guideIV

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/10/14 14:30
 */
class GuideHolderCreator : DiscreteHolderCreator {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun getLayoutId() = R.layout.layout_guide
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="数据加载">
  override fun createHolder(itemView: View): DiscreteHolder<String> = object : DiscreteHolder<String>(itemView) {
    override fun updateUI(data: String?, position: Int, count: Int) {
      val ratio = ScreenUtils.getScreenWidth() * 1f / ScreenUtils.getScreenHeight()
      itemView.guideIV?.loadImgVertical(data, holderRatio = ratio)
      itemView.guideGo?.visibleGone(position == count - 1)
      itemView.guideGo?.let { view ->
        view.visibleGone(position == count - 1)
        view.pressEffectAlpha()
        view.click {
          MMkvUtils.instance.setShowGuide(false)
          MainActivity.startActivity(it.context)
        }
      }
    }
  }
  //</editor-fold>
}