package com.cc.base.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import com.cc.ext.removeParent

/**
 * Author:Khaos
 * Date:2020/8/11
 * Time:18:02
 */
abstract class BaseDialogFragment : DialogFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //是否已经懒加载
  private var isLoaded = false

  //页面基础信息
  lateinit var mContext: Activity
  lateinit var mActivity: Activity
  protected var mRootView: FrameLayout? = null

  //Dilaog信息设置
  var mWidth = LayoutParams.WRAP_CONTENT
  var mHeight = LayoutParams.WRAP_CONTENT
  var mGravity = Gravity.CENTER
  var mAnimation: Int? = null
  var touchOutside: Boolean = true

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="上下文">
  override fun onAttach(context: Context) {
    super.onAttach(context)
    mContext = context as Activity
    mActivity = context
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="创建View">
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
    setDialogStyle()
    return mRootView
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="懒加载核心">
  override fun onResume() {
    super.onResume()
    if (!isLoaded && !isHidden) {
      lazyInit()
      isLoaded = true
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="弹窗样式">
  //设置弹窗样式
  private fun setDialogStyle() {
    dialog?.let { d ->
      //无标题
      d.requestWindowFeature(DialogFragment.STYLE_NO_TITLE)
      //设置外部触摸关闭弹窗
      d.setCanceledOnTouchOutside(touchOutside)
      d.window?.let { w ->
        //透明背景
        w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //去除dialog弹出的阴影
        //w.setDimAmount(0F)
        w.decorView.setPadding(0, 0, 0, 0)
        //弹窗尺寸位置设置
        w.attributes?.let { l ->
          l.width = mWidth
          l.height = mHeight
          l.gravity = mGravity
          w.attributes = l
        }
        //动画
        mAnimation?.let { a -> w.setWindowAnimations(a) }
      }
    }
    //全屏显示dialog
    mRootView?.systemUiVisibility =
      (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
          View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
          View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="UI销毁">
  override fun onDestroyView() {
    super.onDestroyView()
    isLoaded = false
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="子类重写的方法">
  //-----------------------需要重写-----------------------//
  //xml布局
  protected abstract val contentXmlId: Int

  //懒加载初始化
  protected abstract fun lazyInit()
  //</editor-fold>
}