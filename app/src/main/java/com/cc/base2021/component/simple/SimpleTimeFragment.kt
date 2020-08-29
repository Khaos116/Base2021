package com.cc.base2021.component.simple

import androidx.lifecycle.Observer
import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.utils.RxTimeUtils
import kotlinx.android.synthetic.main.fragment_simple_time.simpleTimeTv
import kotlinx.coroutines.*

/**
 * 获取北京时间展示的Fragment
 * Author:case
 * Date:2020/8/19
 * Time:14:48
 */
class SimpleTimeFragment private constructor() : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(msg: String? = ""): SimpleTimeFragment {
      val fragment = SimpleTimeFragment()
      if (!msg.isNullOrBlank()) fragment.msg = msg
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_simple_time
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var msg = "北京时间获取"
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    simpleTimeTv.text = msg
    simpleTimeTv.append("\n\n系统开机时间:${RxTimeUtils.instance.getOpenTime()}")
    //先移除，防止复用时不会直接获取数据
    RxTimeUtils.instance.firstTimeState.removeObserver(observerFirst)
    RxTimeUtils.instance.allTimeStateByRequest.removeObserver(observerByRequest)
    RxTimeUtils.instance.allTimeStateByResponse.removeObserver(observerByResponse)
    //使用Observer变量是为了移除时好用
    RxTimeUtils.instance.firstTimeState.observe(this, observerFirst)
    RxTimeUtils.instance.allTimeStateByRequest.observe(this, observerByRequest)
    RxTimeUtils.instance.allTimeStateByResponse.observe(this, observerByResponse)
    //发起网络请求
    GlobalScope.launch {
      RxTimeUtils.instance.getFirstResponseTime()
      delay(1000)
      RxTimeUtils.instance.getAllTimeByResponse()
      delay(1000)
      RxTimeUtils.instance.getAllTimeByRequest()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="LiveData数据监听">
  //监听最快返回
  private var observerFirst = Observer<String> {
    simpleTimeTv?.let { tv -> if (!tv.text.toString().contains("并发取响应最快")) tv.append("\n\n并发取响应最快的时间\n$it") }
  }

  //监听串行返回
  private var observerByRequest = Observer<String> {
    simpleTimeTv?.let { tv -> if (!tv.text.toString().contains("串行请求获取")) tv.append("\n\n串行请求获取时间\n$it") }
  }

  //监听并发返回
  private var observerByResponse = Observer<String> {
    simpleTimeTv?.let { tv -> if (!tv.text.toString().contains("并发请求获取")) tv.append("\n\n并发请求获取时间\n$it") }
  }
  //</editor-fold>
}