package com.cc.base2021.component.login

import android.content.Context
import android.content.Intent
import com.cc.base2021.R
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.component.main.MainActivity

/**
 * Author:case
 * Date:2020/8/12
 * Time:17:27
 */
class LoginActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  companion object {
    fun startActivity(context: Context) {
      val intent = Intent(context, LoginActivity::class.java)
      context.startActivity(intent)
    }
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_login
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun initData() {
  }
  //</editor-fold>
}