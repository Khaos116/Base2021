package com.cc.music.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.cc.BuildConfig
import timber.log.Timber

/**
 * https://blog.csdn.net/CrazyMo_/article/details/80250565
 * Author:CASE
 * Date:2020-10-1
 * Time:14:21
 */
abstract class AbstractService : Service() {
  protected var mBinder: IBinder? = null
  override fun onBind(intent: Intent?): IBinder? {
    if (mBinder == null) {
      mBinder = initBinder()
      if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
    return mBinder //与客户端成功连接上的时候返回给客户端使用的对象
  }

  protected abstract fun initBinder(): IBinder
}