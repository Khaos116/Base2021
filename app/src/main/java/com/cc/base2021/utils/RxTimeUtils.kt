package com.cc.base2021.utils

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import org.json.JSONObject
import rxhttp.*
import rxhttp.wrapper.param.RxHttp
import timber.log.Timber
import java.lang.StringBuilder

/**
 * Author:case
 * Date:2020/8/18
 * Time:14:51
 */
class RxTimeUtils private constructor() {

  //<editor-fold defaultstate="collapsed" desc="内部变量">
  //苏宁易购
  private val suNing = "https://f.m.suning.com/api/ct.do"

  //京东
  private val jingDong = "https://a.jd.com//ajax/queryServerData.html"

  //淘宝
  private val taoBao = "http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp"

  //腾讯
  private val tencent = "http://vv.video.qq.com/checktime?otype=json"

  //内部LiveData
  private val firstTime = MutableLiveData<String>()
  private val allTimeByRequest = MutableLiveData<String>()
  private val allTimeByResponse = MutableLiveData<String>()
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="单利">
  private object SingletonHolder {
    val holder = RxTimeUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部访问">
  //获取北京时间
  val firstTimeState: LiveData<String>
    get() = firstTime

  //获取所有时间，请求顺序
  val allTimeStateByRequest: LiveData<String>
    get() = allTimeByRequest

  //获取所有时间，响应顺序
  val allTimeStateByResponse: LiveData<String>
    get() = allTimeByResponse

  //获取系统开机时间
  fun getOpenTime(): String {
    //从设备开机到现在的时间，单位毫秒，含系统深度睡眠时间
    val time1 = SystemClock.elapsedRealtime()
    //手机系统时间，单位毫秒，可以在手机设置中修改时间
    val time2 = System.currentTimeMillis()
    //通过时差拿到开机时间
    return TimeUtils.millis2String(time2 - time1)
  }

  //获取响应速度最快的北京时间(协程Select方法)
  fun getFirstResponseTime() {
    GlobalScope.launch(Dispatchers.Main) {
      val suNingDeferred = async { getTimeFromSuNing() }
      val jingDongDeferred = async { getTimeFromJingDong() }
      val taoBaoDeferred = async { getTimeFromTaoBao() }
      val tencentDeferred = async { getTimeFromTencent() }
      firstTime.value = select<String> {
        suNingDeferred.onAwait { it }
        jingDongDeferred.onAwait { it }
        taoBaoDeferred.onAwait { it }
        tencentDeferred.onAwait { it }
      }
    }
  }

  fun getAllTimeByRequest() {
    GlobalScope.launch(Dispatchers.Main) {
      val sb = StringBuilder()
      sb.append(getTimeFromSuNing()).append("\n")
        .append(getTimeFromJingDong()).append("\n")
        .append(getTimeFromTaoBao()).append("\n")
        .append(getTimeFromTencent())
      allTimeByRequest.value = sb.toString()
    }
  }

  fun getAllTimeByResponse() {
    val sb = StringBuilder()
    GlobalScope.launch(Dispatchers.Main) {
      sb.append(getTimeFromSuNing())
      checkSb(sb)
    }
    GlobalScope.launch(Dispatchers.Main) {
      sb.append(getTimeFromJingDong())
      checkSb(sb)
    }
    GlobalScope.launch(Dispatchers.Main) {
      sb.append(getTimeFromTaoBao())
      checkSb(sb)
    }
    GlobalScope.launch(Dispatchers.Main) {
      sb.append(getTimeFromTencent())
      checkSb(sb)
    }
  }

  //判断是否请求完成
  private fun checkSb(sb: StringBuilder) {
    if (sb.count { c -> c.toString() == "\n" } == 3) {
      allTimeByResponse.value = sb.toString()
    } else {
      sb.append("\n")
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="从不同平台获取当前北京时间">

  //从苏宁获取时间
  private suspend fun getTimeFromSuNing(): String {
    return RxHttp.get(suNing)
      .toStr()
      .map { s ->
        val js = JSONObject(s)
        js.optLong("currentTime", 0)
      }
      .map { t ->
        if (t == 0L) {
          "苏宁获取时间失败"
        } else {
          "苏宁时间:${TimeUtils.millis2String(t)}"
        }
      }
      .onErrorReturnItem("苏宁获取时间失败")
      .await()
  }

  //从京东获取时间
  private suspend fun getTimeFromJingDong(): String {
    return RxHttp.get(jingDong)
      .toStr()
      .map { s ->
        val js = JSONObject(s)
        js.optLong("serverTime", 0)
      }
      .map { t ->
        if (t == 0L) {
          "京东获取时间失败"
        } else {
          "京东时间:${TimeUtils.millis2String(t)}"
        }
      }
      .onErrorReturnItem("京东获取时间失败")
      .await()
  }

  //从淘宝获取时间
  private suspend fun getTimeFromTaoBao(): String {
    return RxHttp.get(taoBao)
      .toStr()
      .map { s ->
        val js = JSONObject(s)
        val data = js.optString("data")
        val o = JSONObject(data)
        o.optLong("t")
      }
      .map { t ->
        if (t == 0L) {
          "淘宝获取时间失败"
        } else {
          "淘宝时间:${TimeUtils.millis2String(t)}"
        }
      }
      .onErrorReturnItem("淘宝获取时间失败")
      .await()
  }

  //从腾讯获取时间
  private suspend fun getTimeFromTencent(): String {
    return RxHttp.get(tencent)
      .toStr()
      .map { s ->
        val index1 = s.indexOf("{")
        val index2 = s.lastIndexOf("}") + 1
        val js = JSONObject(s.substring(index1, index2))
        js.optLong("t", 0) * 1000
      }
      .map { t ->
        if (t == 0L) {
          "腾讯获取时间失败"
        } else {
          "腾讯时间:${TimeUtils.millis2String(t)}"
        }
      }
      .onErrorReturnItem("腾讯获取时间失败")
      .await()
  }
  //</editor-fold>
}