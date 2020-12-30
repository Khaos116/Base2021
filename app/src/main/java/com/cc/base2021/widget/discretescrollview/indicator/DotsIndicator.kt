package com.cc.base2021.widget.discretescrollview.indicator

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.SizeUtils
import com.cc.base2021.R

/**
 * ViewPager选中圆点效果 参考：https://github.com/mazenrashed/DotsIndicatorWithoutViewpager/blob/master/dotsindicator/src/main/java/com/mazenrashed/dotsindicator/DotsIndicator.kt
 * Author:CASE
 * Date:2020/8/31
 * Time:9:51
 */
class DotsIndicator : LinearLayout {

  //<editor-fold defaultstate="collapsed" desc="外部可设置的变量">
  //选中后的缩放比
  var selectedDotScaleFactor: Float = 1.4f

  //正常圆点大小
  var dotSize: Int = SizeUtils.dp2px(7f)

  //最后一个图标大小
  var lastDotSize: Int = SizeUtils.dp2px(14f)

  //选中的圆点
  var selectedDotResource: Int = R.drawable.circle_accent

  //未选中的圆点
  var unselectedDotResource: Int = R.drawable.circle_primary

  //最后一个选中的图标
  var lastSelDotResource: Int = R.drawable.ic_home_light_40dp

  //最后一个未选中的图标
  var lastUnselDotResource: Int = R.drawable.ic_home_dark_40dp

  //是否需要最后一个图标为不同图标
  var needSpecial: Boolean = false //是否需要特殊处理最后一个

  var onSelectListener: ((position: Int) -> Unit)? = null
  //</editor-fold>

  private var selection: Int = 0
  private var dotsCount: Int = 0
  private var marginsBetweenDots: Int = SizeUtils.dp2px(4f)

  constructor(context: Context?) : super(context) {
    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    gravity = Gravity.CENTER
  }

  constructor(
    context: Context?,
    attrs: AttributeSet
  ) : super(context, attrs) {
    layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    gravity = Gravity.CENTER

    val ta = getContext().obtainStyledAttributes(attrs, R.styleable.DotsIndicator, 0, 0)
    dotsCount = ta.getInt(R.styleable.DotsIndicator_dots_count, 3)

    selectedDotScaleFactor = ta.getFloat(R.styleable.DotsIndicator_selected_dot_scale_factor, 1.4f)

    selectedDotResource = ta.getResourceId(R.styleable.DotsIndicator_selected_dot_resource, selectedDotResource)

    unselectedDotResource = ta.getResourceId(R.styleable.DotsIndicator_unselected_dot_resource, unselectedDotResource)

    lastSelDotResource = ta.getResourceId(R.styleable.DotsIndicator_last_selected_dot_resource, lastSelDotResource)

    lastUnselDotResource = ta.getResourceId(R.styleable.DotsIndicator_last_unselected_dot_resource, lastUnselDotResource)

    dotSize = ta.getDimensionPixelSize(R.styleable.DotsIndicator_dot_size, dotSize)

    lastDotSize = ta.getDimensionPixelSize(R.styleable.DotsIndicator_last_dot_size, lastDotSize)

    marginsBetweenDots = ta.getDimensionPixelSize(R.styleable.DotsIndicator_margins_between_dots, marginsBetweenDots)
    initDots(dotsCount)
    ta.recycle()
  }

  init {
    //采用放大的模式会导致图片模糊，所以一开始把图标设大，显示的时候缩小，然后选中再放大，这样就不会模糊了
    dotSize = (dotSize * selectedDotScaleFactor).toInt()
    lastDotSize = (lastDotSize * selectedDotScaleFactor).toInt()
  }

  //当不需要特殊的时候需要设置间距(+1是为了防止放大显示不全，不能去除小数，只能往上加)
  private val marginNoSpecial = (dotSize * (selectedDotScaleFactor - 1) / 2f).toInt() + 1

  //需要特殊的时候需要设置间距(+1是为了防止放大显示不全，不能去除小数，只能往上加)
  private val marginSpecial = (lastDotSize * (selectedDotScaleFactor - 1) / 2f).toInt() + 1
  fun initDots(dotsCount: Int) {
    this.dotsCount = dotsCount
    removeAllViews()
    if (dotsCount <= 1) return
    for (i: Int in 0 until dotsCount) {
      val dot = ImageView(context)
      dot.id = i
      dot.tag = i
      val margin = if (needSpecial) marginSpecial else marginNoSpecial
      val param =
        if (i == dotsCount - 1 && needSpecial) {
          LayoutParams(lastDotSize, lastDotSize)
        } else {
          LayoutParams(dotSize, dotSize)
        }
      if (orientation == HORIZONTAL) {
        param.marginEnd = marginsBetweenDots / 2
        param.marginStart = marginsBetweenDots / 2
        param.topMargin = margin
        param.bottomMargin = margin
        param.gravity = Gravity.CENTER_VERTICAL
      } else {
        param.marginEnd = margin
        param.marginStart = margin
        param.topMargin = marginsBetweenDots / 2
        param.bottomMargin = marginsBetweenDots / 2
        param.gravity = Gravity.CENTER_HORIZONTAL
      }
      dot.layoutParams = param
      dot.scaleType = ImageView.ScaleType.FIT_XY

      if (i == dotsCount - 1 && needSpecial) {
        if (selection == dotsCount - 1) {
          dot.setImageResource(lastSelDotResource)
        } else {
          dot.setImageResource(lastUnselDotResource)
        }
      } else {
        if (selection == i) {
          dot.setImageResource(selectedDotResource)
        } else {
          dot.setImageResource(unselectedDotResource)
        }
      }

      if (selection == i) {
        dot.scaleX = 1f
        dot.scaleY = 1f
      } else {
        dot.scaleX = 1f / selectedDotScaleFactor
        dot.scaleY = 1f / selectedDotScaleFactor
      }

      dot.setOnClickListener {
        onSelectListener?.invoke(it.tag as Int)
        setDotSelection(it.tag as Int)
      }
      addView(dot)
    }
    setDotSelection(selection)
  }

  fun setDotSelection(position: Int) {
    if (position == selection)
      return
    val newSelection: ImageView = findViewById(position)
    val selectedDot: ImageView = findViewWithTag(selection)

    val increaseAnimator = ValueAnimator.ofFloat(1f / selectedDotScaleFactor, 1f) //变大
    val decreaseAnimator = ValueAnimator.ofFloat(1f, 1f / selectedDotScaleFactor) //缩小

    increaseAnimator.addUpdateListener { animator ->
      val value: Float = animator.animatedValue as Float
      newSelection.scaleX = value
      newSelection.scaleY = value
    }

    decreaseAnimator.addUpdateListener {
      val value: Float = it.animatedValue as Float
      selectedDot.scaleX = value
      selectedDot.scaleY = value
    }

    increaseAnimator.start()
    decreaseAnimator.start()

    val animationListener = object : Animator.AnimatorListener {
      override fun onAnimationRepeat(animation: Animator?) {}

      override fun onAnimationEnd(animation: Animator?) {
        newSelection.scaleX = 1f
        newSelection.scaleY = 1f

        selectedDot.scaleX = 1f / selectedDotScaleFactor
        selectedDot.scaleY = 1f / selectedDotScaleFactor
      }

      override fun onAnimationCancel(animation: Animator?) {}

      override fun onAnimationStart(animation: Animator?) {}
    }

    increaseAnimator.addListener(animationListener)
    decreaseAnimator.addListener(animationListener)
    newSelection.setImageResource(
      if (newSelection.tag == dotsCount - 1 && needSpecial) lastSelDotResource else selectedDotResource
    )
    selectedDot.setImageResource(
      if (selection == dotsCount - 1 && needSpecial) lastUnselDotResource else unselectedDotResource
    )
    selection = newSelection.tag as Int
  }
}