package com.cc.base2021.component.main.fragment

import android.graphics.Color
import android.graphics.Typeface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.cc.base.ui.BaseFragment
import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.component.simple.SimpleTimeFragment
import kotlinx.android.synthetic.main.fragment_home.homeIndicator
import kotlinx.android.synthetic.main.fragment_home.homePager

/**
 * Author:case
 * Date:2020/8/12
 * Time:20:50
 */
class HomeFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="变量">

  //子Fragment
  private var fragments: MutableList<BaseFragment> = mutableListOf()

  //子Fragment标题
  private var titles: MutableList<String> = mutableListOf()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_home
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  @Suppress("DEPRECATION")
  override fun lazyInit() {
    if (titles.isEmpty()) {
      titles.add("Girl")
      titles.add("Article")
      titles.add("Time")
      fragments = mutableListOf(
        GirlFragment.newInstance(),
        GankFragment.newInstance(),
        SimpleTimeFragment.newInstance()
      )
    }
    homePager.adapter = object : FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
      override fun getItem(position: Int): Fragment {
        return fragments[position]
      }

      override fun getCount(): Int {
        return fragments.size
      }

      override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
      }
    }
    homeIndicator.setExpand(true) //设置tab宽度为包裹内容还是平分父控件剩余空间，默认值：false,包裹内容
      //.setIndicatorWrapText(true) //设置indicator是与文字等宽还是与整个tab等宽，默认值：true,与文字等宽
      .setTabWidth(60, 0) //设置固定的指示器宽度和圆角
      .setIndicatorColor(Color.parseColor("#AF2121")) //indicator颜色
      .setIndicatorHeight(2) //indicator高度
      .setShowUnderline(true, Color.parseColor("#eeeeee"), 1f) //设置是否展示underline，默认不展示
      //.setShowDivider(false, getResColor(R.color.c_a28dff), 10, 1) //设置是否展示分隔线，默认不展示
      .setTabTextSize(16) //文字大小
      .setTabTextColor(Color.parseColor("#999999")) //文字颜色
      .setTabTypeface(null) //字体
      .setTabTypefaceStyle(Typeface.BOLD) //字体样式：粗体、斜体等
      .setTabBackgroundResId(0) //设置tab的背景
      .setTabPadding(16) //设置tab的左右padding
      .setSelectedTabTextSize(16) //被选中的文字大小
      .setSelectedTabTextColor(Color.parseColor("#AF2121")) //被选中的文字颜色
      .setSelectedTabTypeface(null)
      .setSelectedTabTypefaceStyle(Typeface.BOLD)
      .setTabTransY(0f) //导航器偏移量
      .setTextTransY(3f) //tab文字偏移量
      .setScrollOffset(120) //滚动偏移量
      .setViewPager(homePager)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="公共方法调用">
  override fun scroll2Top() {
    fragments[homePager.currentItem].scroll2Top()
  }
  //</editor-fold>
}