package com.cc.base2021.comm

import android.os.Bundle
import com.cc.base.ui.BaseActivity

/**
 * Author:Khaos
 * Date:2020/8/11
 * Time:18:29
 */
abstract class CommActivity : BaseActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    //清理奔溃前的fragment
    for (fragment in supportFragmentManager.fragments) {
      supportFragmentManager.beginTransaction()
        .remove(fragment)
        .commitAllowingStateLoss()
    }
    super.onCreate(savedInstanceState)
  }
}