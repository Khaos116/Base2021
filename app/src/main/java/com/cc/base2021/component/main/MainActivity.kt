package com.cc.base2021.component.main

import com.blankj.utilcode.util.AppUtils
import com.cc.base.ext.click
import com.cc.base2021.R.layout
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.dialog.actionDialog
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : CommActivity() {
  override fun layoutResId() = layout.activity_main

  override fun initView() {
    mainTv.text = AppUtils.getAppPackageName()
    Timber.e("CASE-主页初始化完成")
    mainTv.click { actionDialog(supportFragmentManager) {} }
  }

  override fun initData() {
  }
}