package com.cc.base2021.comm

import android.view.View
import android.view.ViewGroup
import com.cc.base.ext.*
import com.cc.base2021.R
import kotlinx.android.synthetic.main.activity_comm_title.commonRootView
import kotlinx.android.synthetic.main.layout_comm_title.commTitleBack
import kotlinx.android.synthetic.main.layout_comm_title.commTitleText

/**
 * Author:case
 * Date:2020/8/21
 * Time:9:34
 */
abstract class CommTitleActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_comm_title
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="添加子View">
  override fun initView() {
    //处理返回
    commTitleBack.pressEffectAlpha()
    commTitleBack.click { finish() }
    //添加子view
    if (commonRootView.childCount == 1) {
      commonRootView.addView(
        View.inflate(mContext, layoutResContentId(), null),
        ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
      )
    }
    //调用初始化
    initContentView()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类直接调用">
  //设置标题
  fun setTitleText(title: CharSequence) {
    commTitleText.text = title
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重写">
  //子xml
  abstract fun layoutResContentId(): Int

  //子控件初始化
  abstract fun initContentView()
  //</editor-fold>
}