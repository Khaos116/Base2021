package com.cc.base2021.startup

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.startup.Initializer
import com.billy.android.swipe.SmartSwipeBack
import com.billy.android.swipe.SmartSwipeRefresh
import com.cc.ext.logI
import com.cc.startup.UtilsInit
import com.cc.base2021.component.guide.GuideActivity
import com.cc.base2021.component.login.LoginActivity
import com.cc.base2021.component.main.MainActivity
import com.cc.base2021.component.splash.SplashActivity

/**
 * Author:case
 * Date:2020/8/19
 * Time:17:11
 */
class SwipeInit : Initializer<Int> {
  override fun create(context: Context): Int {
    initSmartSwipeBack(context.applicationContext as Application)
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(UtilsInit::class.java)
  }

  //静态代码段可以防止内存泄露
  companion object {
    init {
      //设置全局的Header构建器
      SmartSwipeRefresh.setDefaultRefreshViewCreator(object :
        SmartSwipeRefresh.SmartSwipeRefreshViewCreator {
        override fun createRefreshHeader(context: Context): SmartSwipeRefresh.SmartSwipeRefreshHeader {
          return com.billy.android.swipe.refresh.ClassicHeader(context)
            .apply { setBackgroundColor(Color.parseColor("#f3f3f3")) }
        }

        override fun createRefreshFooter(context: Context): SmartSwipeRefresh.SmartSwipeRefreshFooter {
          return com.cc.base2021.widget.swipe.ClassicFooter(context)
            .apply { setBackgroundColor(Color.parseColor("#f3f3f3")) }
        }
      })
    }
  }

  //侧滑返回
  private fun initSmartSwipeBack(application: Application) {
    /*
    //仿手机QQ的手势滑动返回
    SmartSwipeBack.activityStayBack(application, null);
    //仿微信带联动效果的透明侧滑返回
    SmartSwipeBack.activitySlidingBack(application, null);
    //侧滑开门样式关闭activity
    SmartSwipeBack.activityDoorBack(application, null);
    //侧滑百叶窗样式关闭activity
    SmartSwipeBack.activityShuttersBack(application, null);
    //仿小米MIUI系统的贝塞尔曲线返回效果
    SmartSwipeBack.activityBezierBack(application, null);
     */
    SmartSwipeBack.activitySlidingBack(application) { activity -> !list.contains(activity.javaClass.name) }
  }

  //不需要侧滑的页面
  private val list = listOf(
    SplashActivity::class.java.name,
    MainActivity::class.java.name,
    GuideActivity::class.java.name,
    LoginActivity::class.java.name,
    "com.didichuxing.doraemonkit.ui.UniversalActivity"
  )
}