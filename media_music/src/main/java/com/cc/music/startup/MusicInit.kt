package com.cc.music.startup

import android.app.Application
import android.app.Service
import android.content.*
import android.os.IBinder
import androidx.startup.Initializer
import com.blankj.utilcode.util.ServiceUtils.bindService
import com.blankj.utilcode.util.Utils
import com.cc.ext.logI
import com.cc.music.IMusicOperate
import com.cc.music.service.MusicPlayService
import com.cc.startup.UtilsInit

/**
 * Author:Khaos
 * Date:2020-10-1
 * Time:16:30
 */
class MusicInit : Initializer<Int> {
  //<editor-fold defaultstate="collapsed" desc="初始化及依赖">
  override fun create(context: Context): Int {
    bindMusicService()
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(UtilsInit::class.java)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="绑定播放服务">
  private fun bindMusicService() {
    val intent = Intent(Utils.getApp(), MusicPlayService::class.java)
    Utils.getApp().bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE)
  }

  private var mServiceConnection: ServiceConnection = object : ServiceConnection {
    override fun onServiceConnected(name: ComponentName, service: IBinder) {
      Utils.getApp().mMusicPlayerService = IMusicOperate.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName) {
      Utils.getApp().mMusicPlayerService?.playRelease()
      Utils.getApp().mMusicPlayerService = null
    }
  }
  //</editor-fold>
}

//<editor-fold defaultstate="collapsed" desc="扩展Application获取播放操作">
//从Application获取播放服务
private var musicPlayerService: IMusicOperate? = null
var Application.mMusicPlayerService: IMusicOperate?
  get() {
    return musicPlayerService
  }
  set(value) {
    musicPlayerService = value
  }
//</editor-fold>