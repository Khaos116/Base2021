package com.cc.base.ext

import android.content.Context
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.blankj.utilcode.util.*

/**
 * Author:case
 * Date:2020/8/11
 * Time:17:44
 */

//屏幕宽度
val Context.mScreenWidth: Int
  get() {
    return ScreenUtils.getScreenWidth()
  }

//屏幕高度
val Context.mScreenHeight: Int
  get() {
    return ScreenUtils.getScreenHeight()
  }

//状态栏高度
val Context.mStatusBarHeight: Int
  get() {
    return BarUtils.getStatusBarHeight()
  }

//dp转px
fun Context.dp2px(dp: Float): Int {
  return SizeUtils.dp2px(dp)
}

//dp转px
fun Context.dp2px(dp: Int): Int {
  return SizeUtils.dp2px(dp.toFloat())
}

//XML的layout转换为View
fun Context.inflate(
  @LayoutRes layoutResource: Int,
  parent: ViewGroup? = null,
  attachToRoot: Boolean = false
): View {
  return LayoutInflater.from(this).inflate(layoutResource, parent, attachToRoot)
}

//吐司
fun Context.toast(@StringRes resId: Int) {
  StringUtils.getString(resId).toast()
}

//吐司
fun Context.toast(msg: String?) {
  msg?.toast()
}