package com.cc.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * https://www.jianshu.com/p/a9d09cb7577f
 * description: 闪动的TextView,滑动解锁用.
 * Author:Khaos
 * Date:2020/8/11
 * Time:19:41
 */
class FlashingTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
  private var mWidth = 0
  private var mGradient: LinearGradient? = null
  private var mMatrix: Matrix? = null

  //渐变的速度
  private var deltaX = 0
  private var flashColor = Color.WHITE

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    if (mWidth == 0) {
      mWidth = measuredWidth
      //颜色渐变器
      mGradient = LinearGradient(
        0f,
        0f,
        mWidth * 1f,
        0f,
        intArrayOf(currentTextColor, flashColor, currentTextColor),
        floatArrayOf(0f, 0.5f, 1.0f),
        Shader.TileMode.CLAMP
      )
      paint.shader = mGradient
      mMatrix = Matrix()
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (mMatrix != null) {
      deltaX += mWidth / 5
      if (deltaX > 2 * mWidth) {
        deltaX = -mWidth
      }
    }
    //关键代码通过矩阵的平移实现
    mMatrix?.setTranslate(deltaX.toFloat(), 0f)
    mGradient?.setLocalMatrix(mMatrix)
    //postInvalidateDelayed(120)
    removeCallbacks(runable)
    postDelayed(runable, 120)
  }

  private var runable = Runnable {
    postInvalidate()
  }

  fun setFlashColor(flashColor: Int) {
    this.flashColor = flashColor
    postInvalidate()
  }
}