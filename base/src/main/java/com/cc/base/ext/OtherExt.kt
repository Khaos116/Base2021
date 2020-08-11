package com.cc.base.ext

import android.app.Activity
import android.content.Context
import android.graphics.Color
import androidx.fragment.app.FragmentHostCallback
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.Utils
import com.cc.base.app.BaseApplication

/**
 * Author:case
 * Date:2020/8/11
 * Time:17:46
 */
//通过FragmentManager获取Context
fun FragmentManager.getContext(): Context? {
  return try {
    val field = this.javaClass.getDeclaredField("mHost")
    field.isAccessible = true
    val temp = field.get(this) as FragmentHostCallback<*>
    temp.onGetLayoutInflater().context as Activity
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }
}

//获取BaseApplication
fun Utils.getApplication(): BaseApplication {
  return Utils.getApp() as BaseApplication
}

/*产生随机颜色(不含透明度)*/
fun Color.randomNormal(): Int {
  val r = (Math.random() * 256).toInt()
  val g = (Math.random() * 256).toInt()
  val b = (Math.random() * 256).toInt()
  return Color.rgb(r, g, b)
}

/*产生随机颜色(含透明度)*/
fun Color.randomAlpha(): Int {
  val a = (Math.random() * 256).toInt()
  val r = (Math.random() * 256).toInt()
  val g = (Math.random() * 256).toInt()
  val b = (Math.random() * 256).toInt()
  return Color.argb(a, r, g, b)
}