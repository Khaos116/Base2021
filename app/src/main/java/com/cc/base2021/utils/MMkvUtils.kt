package com.cc.base2021.utils

import com.tencent.mmkv.MMKV

/**
 * Author:Khaos
 * Date:2020/8/12
 * Time:15:12
 */
class MMkvUtils private constructor() {
  //<editor-fold defaultstate="collapsed" desc="单利">
  private object SingletonHolder {
    val holder = MMkvUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="引导页">
  //判断是否要走引导页
  private val NEED_SHOWG_UIDE = "NEED_SHOWG_UIDE"
  fun getShowGuide(): Boolean {
    return MMKV.defaultMMKV().decodeBool(NEED_SHOWG_UIDE, true)
  }

  fun setShowGuide(needShow: Boolean = false) {
    MMKV.defaultMMKV().encode(NEED_SHOWG_UIDE, needShow)
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="TOKEN">
  private val USER_TOKEN = "KKMV_KEY_USER_TOKEN"
  fun getToken(): String? {
    return MMKV.defaultMMKV().decodeString(USER_TOKEN)
  }

  fun setToken(token: String) {
    MMKV.defaultMMKV().encode(USER_TOKEN, token)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="UID">
  private val USER_UID = "KKMV_KEY_USER_UID"
  fun getUid(): Long {
    return MMKV.defaultMMKV().decodeLong(USER_UID, 0L)
  }

  fun setUid(uid: Long) {
    MMKV.defaultMMKV().encode(USER_UID, uid)
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="账号">
  private val USER_ACCOUNT = "USER_ACCOUNT"
  fun getAccount(): String {
    return MMKV.defaultMMKV().decodeString(USER_ACCOUNT, "")
  }

  fun setAccount(account: String) {
    MMKV.defaultMMKV().encode(USER_ACCOUNT, account)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="密码">
  private val USER_PWD = "USER_PWD"
  fun getPassword(): String {
    return MMKV.defaultMMKV().decodeString(USER_PWD, "")
  }

  fun setPassword(pwd: String) {
    MMKV.defaultMMKV().encode(USER_PWD, pwd)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="清除登录信息">
  fun clearUserInfo() {
    MMKV.defaultMMKV().removeValueForKey(USER_UID)
    MMKV.defaultMMKV().removeValueForKey(USER_TOKEN)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="Gank图片信息">
  fun saveGankImageUrl(key: String, url: String) {
    MMKV.mmkvWithID("GankImage")
      .encode(key, url)
  }

  fun getGankImageUrl(key: String): String? {
    return MMKV.mmkvWithID("GankImage").decodeString(key)
  }
  //</editor-fold>
}