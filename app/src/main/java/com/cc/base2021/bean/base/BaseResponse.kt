package com.cc.base2021.bean.base

/**
 * Description:网络请求返回的基类
 * @author: Khaos
 * @date: 2019/9/22 18:54
 */
data class BaseResponse<out T>(
  var errorCode: Int = -1,//正常接口使用的状态码
  var errorMsg: String? = null,//code异常对应的信息提示
  val data: T? = null//正常返回的数据信息
)
