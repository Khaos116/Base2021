package com.cc.base2021.comm

import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.cc.base.ui.BaseFragment
import com.cc.base2021.R
import com.cc.base2021.widget.FlashingTextView
import com.cc.ext.click
import com.cc.ext.removeParent

/**
 * Author:case
 * Date:2020/8/12
 * Time:9:48
 */
abstract class CommFragment : BaseFragment() {
  private var loadingView: FlashingTextView? = null
  private var errorView: TextView? = null

  protected fun showLoadingView(msg: String = "") {
    errorView?.removeParent()
    if (loadingView == null) {
      loadingView = FlashingTextView(mContext)
      loadingView?.run {
        text = StringUtils.getString(R.string.action_loading)
        gravity = Gravity.CENTER
        setTextColor(ColorUtils.getColor(R.color.gray_444444))
      }
    }
    if (msg.isNotBlank()) loadingView?.text = msg
    mRootView?.addView(loadingView, ViewGroup.LayoutParams(-1, -1))
  }

  protected fun dismissLoadingView() {
    loadingView?.removeParent()
  }

  protected fun showErrorView(msg: String? = "", retry: (() -> Unit)? = null) {
    loadingView?.removeParent()
    if (errorView == null) {
      errorView = TextView(mContext)
      errorView?.run {
        text = StringUtils.getString(R.string.load_fail_retry)
        gravity = Gravity.CENTER
        setTextColor(ColorUtils.getColor(R.color.gray_444444))
      }
    }
    if (!msg.isNullOrBlank()) errorView?.text = msg
    if (retry != null) errorView?.click { retry.invoke() }
    else errorView?.setOnClickListener(null)
    mRootView?.addView(errorView, ViewGroup.LayoutParams(-1, -1))
  }
}