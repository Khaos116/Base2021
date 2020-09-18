package com.cc.resource.widget

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils.TruncateAt.MARQUEE
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/23 18:02
 */
class MarqueeTextView @kotlin.jvm.JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

  init {
    isFocusable = true
    isFocusableInTouchMode = true
    setSingleLine()
    ellipsize = MARQUEE
    marqueeRepeatLimit = -1
    onWindowFocusChanged(true)
  }

  override fun isFocused() = true

  override fun onFocusChanged(
    focused: Boolean,
    direction: Int,
    previouslyFocusedRect: Rect?
  ) {
    super.onFocusChanged(true, direction, previouslyFocusedRect)
  }

  override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
    super.onWindowFocusChanged(true)
  }
}