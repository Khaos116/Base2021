package com.cc.base.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.cc.base.ext.logI
import com.cc.base.ext.removeParent
import com.cc.base.utils.CleanLeakUtils

/**
 * Author:case
 * Date:2020/8/11
 * Time:18:01
 */
abstract class BaseFragment : Fragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //是否已经懒加载
  private var isLoaded = false
  private var isFirst = true

  //页面基础信息
  lateinit var mContext: Activity
  lateinit var mActivity: Activity
  protected var mRootView: FrameLayout? = null

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="上下文">
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
    mActivity = context
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建View(重用)">
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //第一次的时候加载xml
    if (contentXmlId > 0 && mRootView == null) {
      val contentView = inflater.inflate(contentXmlId, null)
      if (contentView is FrameLayout) {
        contentView.layoutParams = ViewGroup.LayoutParams(-1, -1)
        mRootView = contentView
      } else {
        mRootView = FrameLayout(mContext)
        mRootView?.layoutParams = ViewGroup.LayoutParams(-1, -1)
        mRootView?.addView(contentView, ViewGroup.LayoutParams(-1, -1))
      }
    } else {
      //防止重新create时还存在
      mRootView?.removeParent()
    }
    return mRootView
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载核心">
  override fun onResume() {
    super.onResume()
    if (!isLoaded && !isHidden) {
      "Fragment:懒加载UI：${this}".logI()
      lazyInitViewXTime(isFirst)
      if (isFirst) {
        "Fragment:懒加载数据：${this}".logI()
        lazyInitData1Time()
      }
      isFirst = false
      isLoaded = true
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="UI销毁-重置懒加载">
  override fun onDestroyView() {
    super.onDestroyView()
    isLoaded = false
    "Fragment:UI销毁：${this}".logI()
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="页面销毁释放输入法">
  override fun onDestroy() {
    CleanLeakUtils.instance.fixInputMethodManagerLeak(mActivity)
    "Fragment完全销毁，释放输入法：${this}".logI()
    super.onDestroy()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类需要重新的方法">
  //xml布局
  protected abstract val contentXmlId: Int

  //懒加载初始化
  protected abstract fun lazyInitViewXTime(isFirst: Boolean) //可能加载多次，如果View销毁，则重新初始化
  protected abstract fun lazyInitData1Time() //只要页面存在，哪怕View销毁也只请求一次
  //</editor-fold>
}