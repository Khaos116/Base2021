package com.cc.base2021.component.simple

import androidx.lifecycle.Observer
import com.cc.base.ext.logE
import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.utils.RxTimeUtils
import kotlinx.android.synthetic.main.fragment_simple.simpleTv

/**
 * Author:case
 * Date:2020/8/11
 * Time:20:29
 */
class SimpleFragment : CommFragment() {
  private var msg = "SimpleFragment"

  companion object {
    fun newInstance(msg: String): SimpleFragment {
      val fragment = SimpleFragment()
      fragment.msg = msg
      return fragment
    }
  }

  override val contentXmlId = R.layout.fragment_simple

  override fun lazyInitView() {
    "SimpleFragment:(${msg}懒加载)".logE()
  }

  override fun lazyInitDta() {
    simpleTv.text = msg
    if (msg == "C") {
      simpleTv.append("\n\n系统开机时间:${RxTimeUtils.instance.getOpenTime()}")
      RxTimeUtils.instance.firstTimeState.observe(this, Observer {
        simpleTv.append("\n\n并发取响应最快的时间\n$it")
      })
      RxTimeUtils.instance.allTimeStateByRequest.observe(this, Observer {
        simpleTv.append("\n\n串行请求获取时间\n$it")
      })
      RxTimeUtils.instance.allTimeStateByResponse.observe(this, Observer {
        simpleTv.append("\n\n并发请求获取时间\n$it")
      })
      RxTimeUtils.instance.getFirstResponseTime()
      RxTimeUtils.instance.getAllTimeByRequest()
      RxTimeUtils.instance.getAllTimeByResponse()
    }
  }
}