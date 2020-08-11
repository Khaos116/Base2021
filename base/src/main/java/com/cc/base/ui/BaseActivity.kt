package com.cc.base.ui

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.cc.base.R
import com.cc.base.utils.CleanLeakUtils
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Author:case
 * Date:2020/8/11
 * Time:18:01
 */
abstract class BaseActivity : AppCompatActivity() {
  //<editor-fold defaultstate="collapsed" desc="页面创建">
  override fun onCreate(savedInstanceState: Bundle?) {
    this.onCreateBefore()
    this.initStatus()
    super.onCreate(savedInstanceState)
    setContentView(layoutResId())
    initView()
    initData()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="页面销毁-释放输入法内存泄漏">
  override fun onDestroy() {
    CleanLeakUtils.instance.fixInputMethodManagerLeak(this)
    super.onDestroy()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类可重新的方法">
  protected open fun onCreateBefore() {}

  protected open fun initStatus() {
    immersionBar {
      statusBarDarkFont(true)
      statusBarColor(R.color.style_Accent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类必须重新的方法">
  //xml布局
  @LayoutRes
  protected abstract fun layoutResId(): Int

  //初始化View
  protected abstract fun initView()

  //初始化数据
  protected abstract fun initData()
  //</editor-fold>
}