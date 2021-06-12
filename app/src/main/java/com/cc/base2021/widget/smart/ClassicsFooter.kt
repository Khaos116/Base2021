package com.cc.base2021.widget.smart

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import com.scwang.smart.refresh.footer.ClassicsFooter

/**
 * Author:Khaos
 * Date:2020-11-27
 * Time:16:04
 */
class ClassicsFooter(context: Context, attrs: AttributeSet? = null)
  : ClassicsFooter(context, attrs) {
  override fun setPrimaryColors(vararg colors: Int) {
    if (colors.isNotEmpty()) {
      val thisView: View = this
      if (thisView.background !is BitmapDrawable && !mSetPrimaryColor) {
        setPrimaryColor(colors[0])
        mSetPrimaryColor = false
      }
      if (!mSetAccentColor) {
        if (colors.size > 1) {
          setAccentColor(colors[1])
        }
        mSetAccentColor = false
      }
    }
  }
}
