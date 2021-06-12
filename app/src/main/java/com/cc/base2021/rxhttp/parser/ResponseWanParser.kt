package com.cc.base2021.rxhttp.parser

import com.blankj.utilcode.util.GsonUtils
import com.cc.base2021.bean.base.BasePageList
import com.cc.base2021.bean.base.BaseResponse
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
 * @author: Khaos
 * @date: 2020/6/26 16:01
 */
@Parser(name = "ResponseWan", wrappers = [MutableList::class, BasePageList::class])
open class ResponseWanParser<T> : AbstractParser<T> {
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
    val type: Type = ParameterizedTypeImpl[BaseResponse::class.java, mType]
    val baseResponse = response.convert<BaseResponse<T>>(type)
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
    //val data: BaseResponse<T> = GsonUtils.fromJson(result, type)
    //获取data字段
    val data = baseResponse.data
    //code不等于0，说明数据不正确，抛出异常
    if (baseResponse.errorCode != 0 || data == null) throw ParseException(baseResponse.errorCode.toString(), baseResponse.errorMsg, response)
    return data
  }
}
