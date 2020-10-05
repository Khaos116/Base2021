package com.cc.music.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.Builder
import com.blankj.utilcode.util.*
import com.cc.ResourceApplication
import com.cc.ext.logE
import com.cc.music.R
import com.cc.music.bean.MusicBean
import com.cc.music.enu.*
import com.cc.music.service.MusicPlayService
import com.cc.music.utils.MusicTimeUtils

/**
 * https://github.com/still-soul/LockDemo/blob/master/app/src/main/java/com/ztk/demo/lockdemo/notification/NotificationUtil.java
 * Author:CASE
 * Date:2020-10-3
 * Time:18:45
 */
class MusicNotification {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //通知管理
  private var mNotificationManager: NotificationManager? = null

  //通知创建类
  private var mNotification: Notification? = null

  //通知栏数据设置
  private var mRemoteViews1: RemoteViews? = null
  private var mRemoteViews2: RemoteViews? = null

  //渠道id 安卓8.0 https://blog.csdn.net/MakerCloud/article/details/82079498
  private val mMusicChannelId = AppUtils.getAppPackageName() + ".music.channel.id"
  private val mMusicChannelName = AppUtils.getAppPackageName() + ".music.channel.name"

  //通知
  private var notificationID: Int = 0x123

  //播放器服务
  private var mService: MusicPlayService? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部初始化">
  fun initNotification(service: MusicPlayService) {
    this.mService = service
    if (mNotificationManager == null) {
      mNotificationManager = Utils.getApp().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(mMusicChannelId, mMusicChannelName, NotificationManager.IMPORTANCE_LOW) //等级太高会一直响
        channel.setSound(null, null)
        mNotificationManager?.createNotificationChannel(channel)
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      mNotification = Builder(service, mMusicChannelId)
          .setSmallIcon(R.drawable.svg_media_notification)
          .setWhen(System.currentTimeMillis())
          .setContentIntent(getDefaultIntent(PlayController.DETAIL))
          .setCustomBigContentView(getContentView(true))
          .setCustomContentView(getContentView(false))
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setTicker(StringUtils.getString(R.string.is_playing))
          .setOngoing(true)
          .setChannelId(mMusicChannelId)
          .build()
    } else {
      mNotification = Builder(service, mMusicChannelId)
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(R.drawable.svg_media_notification)
          .setContentIntent(getDefaultIntent(PlayController.DETAIL))
          .setContent(getContentView(false))
          .setPriority(NotificationCompat.PRIORITY_HIGH)
          .setTicker(StringUtils.getString(R.string.is_playing))
          .setOngoing(true)
          .build()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  //隐藏通知
  fun hideNotification() {
    if (!hasShowNotification) return
    mService?.stopForeground(true)
    hasShowNotification = false
    mNotificationManager?.cancel(notificationID)
    "关闭通知栏".logE()
  }

  //刷新歌曲信息
  fun callSongInfo(musicBean: MusicBean) {
    mRemoteViews1?.setTextViewText(R.id.notification_song_name, musicBean.songName ?: StringUtils.getString(R.string.unknown))
    mRemoteViews1?.setTextViewText(R.id.notification_singer_name, musicBean.singerName ?: StringUtils.getString(R.string.unknown))
    mRemoteViews2?.setTextViewText(R.id.notification_song_name, musicBean.songName ?: StringUtils.getString(R.string.unknown))
    mRemoteViews2?.setTextViewText(R.id.notification_singer_name, musicBean.singerName ?: StringUtils.getString(R.string.unknown))
    val app = Utils.getApp()
    if (app is ResourceApplication) app.loadNotificationImage(musicBean.songCover) { bit ->
      mRemoteViews1?.setImageViewBitmap(R.id.notification_cover, bit)
      mRemoteViews2?.setImageViewBitmap(R.id.notification_cover, bit)
    }
    mNotification?.let { n -> mNotificationManager?.notify(notificationID, n) }
  }

  //回调总时长
  fun callDuration(duration: Long) {
    mRemoteViews1?.setTextViewText(R.id.notification_controller_duration, MusicTimeUtils.instance.forMatterMusicTime(duration))
    mRemoteViews2?.setTextViewText(R.id.notification_controller_duration, MusicTimeUtils.instance.forMatterMusicTime(duration))
    mNotification?.let { n -> mNotificationManager?.notify(notificationID, n) }
  }

  private var mProgressSeconds: Long = 0

  //回调播放进度
  fun callProgress(progress: Long) {
    if (mProgressSeconds == progress / 1000) return
    mProgressSeconds = progress / 1000
    mRemoteViews1?.setTextViewText(R.id.notification_controller_time, MusicTimeUtils.instance.forMatterMusicTime(progress))
    mRemoteViews2?.setTextViewText(R.id.notification_controller_time, MusicTimeUtils.instance.forMatterMusicTime(progress))
    mNotification?.let { n -> mNotificationManager?.notify(notificationID, n) }
  }

  private var mPlayState = PlayState.SET_DATA

  //回调播放状态
  fun callPlayState(state: PlayState) {
    if (mPlayState == state) return
    mPlayState = state
    if (state == PlayState.START) showNotification()
    mNotification?.let { n -> mNotificationManager?.notify(notificationID, n) }
  }

  //回调播放模式
  fun callPlayMode(mode: PlayMode) {
    mRemoteViews1?.setImageViewResource(R.id.notification_controller_play_mode,
        when (mode) {
          PlayMode.PLAY_RANDOM -> R.drawable.svg_media_random
          PlayMode.PLAY_IN_ORDER -> R.drawable.svg_media_in_order
          PlayMode.LOOP_ALL -> R.drawable.svg_media_loop_all
          PlayMode.LOOP_ONE -> R.drawable.svg_media_loop_one
        })
    mRemoteViews2?.setImageViewResource(R.id.notification_controller_play_mode,
        when (mode) {
          PlayMode.PLAY_RANDOM -> R.drawable.svg_media_random
          PlayMode.PLAY_IN_ORDER -> R.drawable.svg_media_in_order
          PlayMode.LOOP_ALL -> R.drawable.svg_media_loop_all
          PlayMode.LOOP_ONE -> R.drawable.svg_media_loop_one
        })
    mNotification?.let { n -> mNotificationManager?.notify(notificationID, n) }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部方法">
  private var hasShowNotification = false

  //显示通知
  private fun showNotification() {
    mService?.let { s ->
      mNotificationManager?.let { m ->
        mNotification?.let { n ->
          if (hasShowNotification) return
          hasShowNotification = true
          s.startForeground(notificationID, n)
          m.notify(notificationID, n)
          "显示通知栏".logE()
        }
      }
    }
  }

  // 获取自定义通知栏view
  private fun getContentView(showBigView: Boolean): RemoteViews {
    val pn = AppUtils.getAppPackageName()
    val rv = RemoteViews(pn, if (showBigView) R.layout.notification_music_big else R.layout.notification_music_small)
    rv.setOnClickPendingIntent(R.id.notification_controller_previous, getDefaultIntent(PlayController.PREVIOUS))
    rv.setOnClickPendingIntent(R.id.notification_controller_next, getDefaultIntent(PlayController.NEXT))
    rv.setOnClickPendingIntent(R.id.notification_controller_play_pause, getDefaultIntent(PlayController.PLAY_PAUSE))
    rv.setOnClickPendingIntent(R.id.notification_controller_close, getDefaultIntent(PlayController.CLOSE))
    rv.setOnClickPendingIntent(R.id.notification_root, getDefaultIntent(PlayController.DETAIL))
    if (showBigView) {
      mRemoteViews1 = rv
      mRemoteViews1?.setTextViewText(R.id.notification_app_name, AppUtils.getAppName())
    } else mRemoteViews2 = rv
    return rv
  }

  //PendingIntent获取
  private fun getDefaultIntent(action: PlayController): PendingIntent {
    return PendingIntent.getBroadcast(mService, notificationID, Intent(action.name), PendingIntent.FLAG_UPDATE_CURRENT)
  }
  //</editor-fold>
}