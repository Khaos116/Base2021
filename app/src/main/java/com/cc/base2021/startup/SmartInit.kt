package com.cc.base2021.startup

import android.content.Context
import androidx.startup.Initializer
import com.cc.base2021.R
import com.cc.ext.*
import com.cc.startup.UtilsInit
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Author:CASE
 * Date:2020-11-26
 * Time:18:57
 */
class SmartInit : Initializer<Int> {
  override fun create(context: Context): Int {
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(UtilsInit::class.java)
  }

  //static 代码段可以防止内存泄露 https://github.com/scwang90/SmartRefreshLayout/blob/master/art/md_property.md
  companion object {
    init {
      //设置全局默认配置（优先级最低，会被其他设置覆盖）
      SmartRefreshLayout.setDefaultRefreshInitializer { _, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
        val height = 60.dp2Px() //header和footer的高度
        val color = R.color.gray_eee.xmlToColor() //header和footer的背景色
        layout.layout.post { //直接设置高度没用，所以采用post方式
          layout.refreshHeader?.view?.layoutParams?.height = height
          layout.refreshFooter?.view?.layoutParams?.height = height
          layout.refreshHeader?.view?.setBackgroundColor(color)
          layout.refreshFooter?.view?.setBackgroundColor(color)
        }
        layout.setHeaderHeightPx(height)
        layout.setFooterHeightPx(height)
        layout.setEnableScrollContentWhenLoaded(false) //是否在加载完成时滚动列表显示新的内容
        layout.setEnableRefresh(false) //默认不可下拉
        layout.setEnableLoadMore(false) //默认不可上拉
        layout.setNoMoreData(true) //默认没有更多数据
        layout.setEnableFooterFollowWhenNoMoreData(true) //是否在全部加载结束之后Footer跟随内容
        layout.setHeaderMaxDragRate(1.1f) //下拉最大允许header的高度
        layout.setFooterMaxDragRate(1f) //上拉最大允许footer的高度
        layout.setDragRate(1f) //显示下拉高度/手指真实下拉高度=阻尼效果
        layout.setHeaderTriggerRate(1f) //触发刷新距离 与 HeaderHeight 的比率
      }
      //设置全局的Header构建器
      SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, _ -> ClassicsHeader(context) }
      //设置全局的Footer构建器
      SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ -> ClassicsFooter(context) }
    }
  }
}