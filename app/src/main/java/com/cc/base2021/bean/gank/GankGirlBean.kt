package com.cc.base2021.bean.gank

/**
 * Author:Khaos
 * Date:2020/8/13
 * Time:10:40
 */
data class GankGirlBean(
  var _id: String?, //"5e95922e808d6d2fe6b56ed6",
  var author: String?, //"\u9e22\u5a9b",
  var category: String?, //"Girl",
  var createdAt: String?, //"2020-05-23 08:00:00",
  var desc: String?, //"\u966a\u4f34\u672c\u6765\u5c31\u662f\u8fd9\u4e16\u754c\u4e0a\u6700\u4e86\u4e0d\u8d77\u7684\u5b89\u6170\u200b\u3002",
  var images: MutableList<String?>?, //[],
  var likeCounts: Int = 0, //0,
  var publishedAt: String?, //"2020-05-23 08:00:00",
  var stars: Int = 0, //1,
  var title: String?, //"\u7b2c94\u671f",
  var type: String?, //"Girl",
  var views: Int = 0 //1995
)