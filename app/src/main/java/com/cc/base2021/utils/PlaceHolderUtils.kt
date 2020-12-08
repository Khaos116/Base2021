package com.cc.base2021.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.ResourceUtils
import com.blankj.utilcode.util.ScreenUtils
import com.cc.base2021.R

/**
 * 动态生成loading和error占位图
 * Author:CASE
 * Date:2020-12-8
 * Time:14:30
 */
object PlaceHolderUtils {
  /**
   * 获取loading占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getLoadingHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): LayerDrawable {
    //背景图
    val d1 = ColorDrawable()
    val height = (width * 1f / ratio).toInt()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.loading_square)
    d2.setBounds(0, 0, width, width)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).apply {
      setLayerGravity(0, Gravity.CENTER)
      setLayerGravity(1, Gravity.CENTER)
    }
  }

  /**
   * 获取error占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getErrorHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): LayerDrawable {
    //背景图
    val d1 = ColorDrawable()
    val height = (width * 1f / ratio).toInt()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.error_square)
    d2.setBounds(0, 0, width, width)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).apply {
      setLayerGravity(0, Gravity.CENTER)
      setLayerGravity(1, Gravity.CENTER)
    }
  }
}