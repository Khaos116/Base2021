package com.cc.base2021.constants

/**
 * Description:干货集中营相关开发api地址
 * @author: caiyoufei
 * @date: 2019/10/8 18:48
 */
interface GankUrls {
  companion object {
    //Page从1开始，即使传0也是返回1的数据
    const val ANDROID = "v2/data/category/GanHuo/type/Android/page/%s/count/%s"//{page}/{pageSize}
    const val GIRL = "v2/data/category/Girl/type/Girl/page/%s/count/%s"//{page}/{pageSize}
  }
}