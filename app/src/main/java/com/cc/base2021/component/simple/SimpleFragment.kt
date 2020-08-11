package com.cc.base2021.component.simple

import com.cc.base.ext.logE
import com.cc.base2021.R
import com.cc.base2021.comm.CommActivity
import kotlinx.android.synthetic.main.fragment_simple.simpleTv

/**
 * Author:case
 * Date:2020/8/11
 * Time:20:29
 */
class SimpleFragment : CommActivity() {
  private var msg = "SimpleFragment"

  companion object {
    fun newInstance(msg: String): SimpleFragment {
      val fragment = SimpleFragment()
      fragment.msg = msg
      return fragment
    }
  }

  override fun layoutResId() = R.layout.fragment_simple

  override fun initView() {
    "SimpleFragment(${this.hashCode()})".logE()
    simpleTv.text = msg
  }

  override fun initData() {
  }
}