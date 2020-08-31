package com.cc.base2021.component.guide

import android.content.Context
import android.content.Intent
import com.cc.base2021.R
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.component.guide.adapter.GuideHolderCreator
import com.cc.base2021.constants.ImageUrls
import com.cc.base2021.widget.discretescrollview.DSVOrientation
import com.cc.base2021.widget.discretescrollview.banner.DiscreteBanner
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Author:case
 * Date:2020/8/12
 * Time:15:20
 */
class GuideActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, GuideActivity::class.java)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //引导页图片，如果需要，可以在启动页进行预加载
  private val mList = ImageUrls.instance.imgs.take(4)
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_guide
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏设置">
  override fun initStatus() {
    immersionBar {
      fullScreen(true)
      navigationBarColor(R.color.transparent)
      statusBarDarkFont(true)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    findViewById<DiscreteBanner<String>>(R.id.guideBanner)
      .setOrientation(DSVOrientation.VERTICAL)
      .apply { getIndicator()?.needSpecial = true }
      .setPages(GuideHolderCreator(), mList)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>
}