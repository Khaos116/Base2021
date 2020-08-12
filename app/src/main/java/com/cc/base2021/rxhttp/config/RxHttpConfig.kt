package com.cc.base2021.rxhttp.config

import com.blankj.utilcode.util.Utils
import com.cc.base2021.BuildConfig
import com.cc.base2021.config.HeaderManger
import com.cc.base2021.rxhttp.interceptor.TokenInterceptor
import com.cc.base2021.utils.CharlesUtils
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.cahce.CacheMode
import rxhttp.wrapper.param.Param
import rxhttp.wrapper.param.RxHttp
import rxhttp.wrapper.ssl.*
import java.io.File
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

/**
 * Description:
 * @author: caiyoufei
 * @date: 2020/3/4 15:13
 */
class RxHttpConfig private constructor() {
  private object SingletonHolder {
    val holder = RxHttpConfig()
  }

  companion object {
    val instance =
      SingletonHolder.holder
  }

  private var hasInit = false

  fun init() {
    if (hasInit) return
    hasInit = true
    initRxHttp()
    initRxHttpCahce()
    /**
     * 去除无意义的参数key，这里把header的共同参数剔除
     * @see cc.abase.demo.config.HeaderManger.getStaticHeaders
     */
    deleteCacheParam(
      "Connection",
      "Accept",
      "Content-Type",
      "Charset",
      "request_time"
    )
  }

  //初始化RxHttp https://github.com/liujingxing/okhttp-RxHttp/wiki/%E5%88%9D%E5%A7%8B%E5%8C%96
  private fun initRxHttp() {
    //设置debug模式，默认为false，设置为true后，发请求，过滤"RxHttp"能看到请求日志
    RxHttp.setDebug(BuildConfig.DEBUG)
    //非必须,只能初始化一次，第二次将抛出异常
    RxHttp.init(getRxhttpOkHttpClient())
    //添加公共参数 https://github.com/liujingxing/okhttp-RxHttp/blob/486c7bc9e4554b4604f29c726e3e58714e2de6ee/app/src/main/java/com/example/httpsender/RxHttpManager.java
    RxHttp.setOnParamAssembly { p: Param<*> ->
      p.add("platform", "RxHttp")
      p.addAllHeader(HeaderManger.instance.getStaticHeaders()) //添加公共参数
      //添加Token
      if (HeaderManger.instance.noTokenUrls.filter { u ->
          (p.getHttpUrl()).toString().contains(u, true)
        }.isNullOrEmpty()) {
        HeaderManger.instance.getTokenPair()?.let { p.addHeader(it.first, it.second) }
      }
      p.add("request_time", System.currentTimeMillis()) //添加请求时间，方便更新token
      p
    }
  }

  //OkHttpClient
  private fun getRxhttpOkHttpClient(): OkHttpClient {
    val sslParams = HttpsUtils.getSslSocketFactory()
    val builder = Builder()
      //.cookieJar(CookieStore())//如果启用自动管理，则不需要在TokenInterceptor中进行保存和initRxHttp()进行读取
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
      .hostnameVerifier(
        HostnameVerifier { hostname: String?, session: SSLSession? -> true }
      ) //忽略host验证
    val util = CharlesUtils.getInstance()
    util.setOkHttpCharlesSSL(builder, util.getCharlesInputStream("charles.pem"))
    builder.addInterceptor(TokenInterceptor())
    return builder.build()
  }

  //其他配置获取Okhttp对象
  fun getOkHttpClient(): Builder {
    val sslParams = HttpsUtils.getSslSocketFactory()
    val builder = Builder()
      //.cookieJar(CookieStore())//如果启用自动管理，则不需要在TokenInterceptor中进行保存和initRxHttp()进行读取
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(30, TimeUnit.SECONDS)
      .writeTimeout(30, TimeUnit.SECONDS)
      .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager) //添加信任证书
      .hostnameVerifier(
        HostnameVerifier { hostname: String?, session: SSLSession? -> true }
      ) //忽略host验证
    val util = CharlesUtils.getInstance()
    util.setOkHttpCharlesSSL(builder, util.getCharlesInputStream("charles.pem"))
    return builder
  }

  //缓存配置
  private fun initRxHttpCahce() {
    //设置缓存目录为：Android/data/{app包名目录}/cache/RxHttpCache
    val cacheDir = File(Utils.getApp().externalCacheDir, "RxHttpCache")
    //设置最大缓存为10M，缓存有效时长为1小时
    RxHttpPlugins.setCache(
      cacheDir, 10 * 1024 * 1024L, CacheMode.REQUEST_NETWORK_FAILED_READ_CACHE,
      1 * 60 * 60 * 1000L
    )
  }

  /*
   * https://github.com/liujingxing/okhttp-RxHttp/wiki/%E7%BC%93%E5%AD%98%E7%AD%96%E7%95%A5
   * 缓存时需要生成由参数构成的key，如果参数中有当前时间的参数，则会导致缓存变得无意义，所以需要把这个会导致缓存无意义的参数剔除掉
   */
  private fun deleteCacheParam(vararg params: String) {
    RxHttpPlugins.setExcludeCacheKeys(*params)  //可变参数，可传入多个key
  }
}