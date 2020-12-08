package com.cc.base2021.utils

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
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
  //防止每次都要生成占位图
  private var loadingMaps = hashMapOf<Triple<Float, Int, Int>, LayerDrawable>()
  private var errorMaps = hashMapOf<Triple<Float, Int, Int>, LayerDrawable>()

  /**
   * 获取loading占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getLoadingHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.MAGENTA): LayerDrawable {
    loadingMaps.toList().firstOrNull { it.first.first == ratio && it.first.second == width && it.first.third == bgColor }?.let {
      return it.second
    }
    //背景图
    val d1 = ColorDrawable()
    val height = (width * 1f / ratio).toInt()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.loading_square)
    val size = width.coerceAtMost(height)
    d2.setBounds((width - size) / 2, (height - size) / 2, size, size)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).also { loadingMaps[Triple(ratio, width, bgColor)] = it }
  }

  /**
   * 获取error占位图
   * @param ratio 宽高比(不能设置为0)
   * @param width 控件宽度(默认为屏幕宽度)
   * @param bgColor 占位图背景色
   */
  fun getErrorHolder(ratio: Float = 1f, width: Int = ScreenUtils.getScreenWidth(), @ColorInt bgColor: Int = Color.WHITE): LayerDrawable {
    errorMaps.toList().firstOrNull { it.first.first == ratio && it.first.second == width && it.first.third == bgColor }?.let {
      return it.second
    }
    //背景图
    val d1 = ColorDrawable()
    val height = (width * 1f / ratio).toInt()
    d1.setBounds(0, 0, width, height)
    d1.color = bgColor
    //占位图
    val d2 = ResourceUtils.getDrawable(R.drawable.error_square)
    val size = width.coerceAtMost(height)
    d2.setBounds((width - size) / 2, (height - size) / 2, size, size)
    //生成LayerDrawable
    return LayerDrawable(arrayOf(d1, d2)).also { errorMaps[Triple(ratio, width, bgColor)] = it }
  }
}