package com.cc.base2021.component.simple

import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import kotlinx.android.synthetic.main.fragment_simple.simpleTv

/**
 * 简单Fragment，方便使用时占位
 * Author:case
 * Date:2020/8/11
 * Time:20:29
 */
class SimpleFragment private constructor() : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(msg: String): SimpleFragment {
      val fragment = SimpleFragment()
      fragment.msg = msg
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_simple
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var msg = "SimpleFragment"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    simpleTv.text = msg
  }
  //</editor-fold>
}