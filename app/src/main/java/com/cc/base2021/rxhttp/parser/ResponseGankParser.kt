package com.cc.base2021.rxhttp.parser

import com.blankj.utilcode.util.GsonUtils
import com.cc.base2021.R
import com.cc.base2021.bean.base.GankResponse
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.entity.ParameterizedTypeImpl
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.AbstractParser
import rxhttp.wrapper.utils.convert
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/6/26 11:13
 */
@Parser(name = "ResponseGank", wrappers = [MutableList::class])
open class ResponseGankParser<T> : AbstractParser<T> {
  /**
   * 此构造方法适用于任意Class对象，但更多用于带泛型的Class对象，如：List<Student>
   *
   * 用法:
   * Java: .asParser(new ResponseParser<List<Student>>(){})
   * Kotlin: .asParser(object : ResponseParser<List<Student>>() {})
   *
   * 注：此构造方法一定要用protected关键字修饰，否则调用此构造方法将拿不到泛型类型
   */
  protected constructor() : super()

  /**
   * 此构造方法仅适用于不带泛型的Class对象，如: Student.class
   *
   * 用法
   * Java: .asParser(new ResponseParser<>(Student.class))   或者  .asResponse(Student.class)
   * Kotlin: .asParser(ResponseParser(Student::class.java)) 或者  .asResponse<Student>()
   */
  constructor(type: Type) : super(type)

  @Throws(IOException::class)
  override fun onParse(response: okhttp3.Response): T {
    //获取泛型类型
    val type: Type = ParameterizedTypeImpl[GankResponse::class.java, mType]
    //数据转换
    val gankResponse = response.convert<GankResponse<T>>(type)
    //读取返回结果
    //var result: String? = null
    //val body = response.body
    //if (body != null) {
    //  val source = body.source()
    //  source.request(Long.MAX_VALUE)
    //  result = source.buffer.clone().readString(StandardCharsets.UTF_8)
    //}
    ////判断结果
    //if (result == null || result.isEmpty()) throw ParseException("500", "服务器没有数据", response)
    ////转换类型
    //val data: GankResponse<T> = GsonUtils.fromJson(result, type)
    //获取data字段
    val data = gankResponse.data
    //code不等于0，说明数据不正确，抛出异常
    if (gankResponse.error() || data == null) throw ParseException("-1", if (gankResponse.error()) "接口error" else "数据返回错误", response)
    return data
  }
}