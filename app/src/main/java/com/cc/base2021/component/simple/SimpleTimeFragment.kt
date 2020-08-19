package com.cc.base2021.component.simple

import androidx.lifecycle.Observer
import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.utils.RxTimeUtils
import kotlinx.android.synthetic.main.fragment_simple_time.simpleTimeTv

/**
 * 获取北京时间展示的Fragment
 * Author:case
 * Date:2020/8/19
 * Time:14:48
 */
class SimpleTimeFragment : CommFragment() {

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var msg = "北京时间获取"
  //</editor-fold>

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

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun lazyInitViewXTime(isFirst: Boolean) {
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
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="LiveData数据监听">
  //监听最快返回
  private var observerFirst = Observer<String> {
    simpleTimeTv?.append("\n\n并发取响应最快的时间\n$it")
  }

  //监听串行返回
  private var observerByRequest = Observer<String> {
    simpleTimeTv?.append("\n\n串行请求获取时间\n$it")
  }

  //监听并发返回
  private var observerByResponse = Observer<String> {
    simpleTimeTv?.append("\n\n并发请求获取时间\n$it")
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化Data">
  override fun lazyInitData1Time() {
    //发起网络请求
    RxTimeUtils.instance.getFirstResponseTime()
    RxTimeUtils.instance.getAllTimeByRequest()
    RxTimeUtils.instance.getAllTimeByResponse()
  }
  //</editor-fold>
}