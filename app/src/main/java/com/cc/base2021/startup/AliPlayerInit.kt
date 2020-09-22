package com.cc.base2021.startup

import android.content.Context
import androidx.startup.Initializer
import com.cc.ext.logI
import com.cc.base2021.widget.video.MyAliPlayer
import com.kk.taurus.playerbase.config.PlayerConfig
import com.kk.taurus.playerbase.config.PlayerLibrary
import com.kk.taurus.playerbase.record.PlayRecordManager
import com.kk.taurus.playerbase.record.PlayRecordManager.RecordConfig.Builder

/**
 * https://github.com/jiajunhui/PlayerBase
 * Author:CASE
 * Date:2020-9-17
 * Time:13:49
 */
class AliPlayerInit : Initializer<Int> {
  override fun create(context: Context): Int {
    //如果您想使用默认的网络状态事件生产者，请添加此行配置。
    //并需要添加权限 android.permission.ACCESS_NETWORK_STATE
    PlayerConfig.setUseDefaultNetworkEventProducer(true)
    //初始化库
    PlayerLibrary.init(context)
    //初始化阿里播放器
    MyAliPlayer.init()

    //播放记录的配置
    //开启播放记录
    PlayerConfig.playRecord(true)
    PlayRecordManager.setRecordConfig(Builder().setMaxRecordCount(100).build())
    "初始化完成".logI()
    return 0
  }

  override fun dependencies(): MutableList<Class<out Initializer<*>>> {
    return mutableListOf(MMkvInit::class.java, SwipeInit::class.java)
  }
}