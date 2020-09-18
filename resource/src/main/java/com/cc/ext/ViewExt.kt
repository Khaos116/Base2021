package com.cc.ext

import android.graphics.Color
import android.view.View
import android.view.ViewManager
import com.cc.R
import com.cc.utils.PressEffectHelper

/**
 * Author:case
 * Date:2020/8/11
 * Time:17:33
 */
//击事件
inline fun View.click(crossinline funClick: (view: View) -> Unit) {
  this.setOnClickListener {
    val tag = this.getTag(R.id.id_tag_click)
    if (tag == null || System.currentTimeMillis() - tag.toString().toLong() > 600) {
      this.setTag(R.id.id_tag_click, System.currentTimeMillis())
      funClick.invoke(it)
    }
  }
}

//单击+双击事件
inline fun View.clickAndDouble(
  crossinline funClick: (view: View) -> Unit,
  crossinline funDoubleClick: (view: View) -> Unit
) {
  val clickView = this
  clickView.setOnClickListener {
    val tag1 = clickView.getTag(R.id.id_tag_click)
    val tag2 = clickView.getTag(R.id.id_tag_click_double)
    //触发点击后600ms内不再触发点击
    if (tag2 != null && System.currentTimeMillis() - tag2.toString().toLong() < 600) return@setOnClickListener
    //单击
    if (tag1 == null || System.currentTimeMillis() - tag1.toString().toLong() > 600) {
      clickView.setTag(R.id.id_tag_click, System.currentTimeMillis())
      //倒计时执行单击
      val runnable = Runnable {
        clickView.getTag(R.id.id_tag_click_runnable)?.let { r -> clickView.removeCallbacks(r as Runnable) }
        clickView.setTag(R.id.id_tag_click_double, System.currentTimeMillis())
        funClick.invoke(clickView)
      }
      clickView.setTag(R.id.id_tag_click_runnable, runnable)
      clickView.postDelayed(runnable, 220)
    } else if (System.currentTimeMillis() - tag1.toString().toLong() <= 220) { //双击
      //取消单击，执行双击
      clickView.getTag(R.id.id_tag_click_runnable)?.let { r -> clickView.removeCallbacks(r as Runnable) }
      clickView.setTag(R.id.id_tag_click_double, System.currentTimeMillis())
      funDoubleClick.invoke(clickView)
    }
  }
}

//显示
fun View.visible() {
  this.visibility = View.VISIBLE
}

//不显示，但占位
fun View.invisible() {
  this.visibility = View.INVISIBLE
}

//不显示，不占位
fun View.gone() {
  this.visibility = View.GONE
}

//显示或者不显示且不占位
fun View.visibleGone(visible: Boolean) = if (visible) visible() else gone()

//显示或者不显示但占位
fun View.visibleInvisible(visible: Boolean) = if (visible) visible() else invisible()

//设置按下效果为改变透明度
fun View.pressEffectAlpha(pressAlpha: Float = 0.7f) {
  PressEffectHelper.alphaEffect(this, pressAlpha)
}

//设置按下效果为改变背景色
fun View.pressEffectBgColor(
  bgColor: Int = Color.parseColor("#f7f7f7"),
  topLeftRadiusDp: Float = 0f,
  topRightRadiusDp: Float = 0f,
  bottomRightRadiusDp: Float = 0f,
  bottomLeftRadiusDp: Float = 0f
) {
  PressEffectHelper.bgColorEffect(
    this,
    bgColor,
    topLeftRadiusDp,
    topRightRadiusDp,
    bottomRightRadiusDp,
    bottomLeftRadiusDp
  )
}

//关闭按下效果
fun View.pressEffectDisable() {
  this.setOnTouchListener(null)
}

//从父控件移除
fun View.removeParent() {
  val parentTemp = parent
  if (parentTemp is ViewManager) parentTemp.removeView(this)
}