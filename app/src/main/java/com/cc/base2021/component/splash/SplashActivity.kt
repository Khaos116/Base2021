package com.cc.base2021.component.splash

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.TimeUtils
import com.cc.base2021.R
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.component.guide.GuideActivity
import com.cc.base2021.component.main.MainActivity
import com.cc.base2021.utils.MMkvUtils
import com.cc.base2021.utils.RxUtils
import com.cc.ext.*
import com.cc.utils.PermissionUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*
import com.opensource.svgaplayer.SVGACallback
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.splashSVGA
import kotlinx.android.synthetic.main.activity_splash.splashTv
import java.util.concurrent.TimeUnit

/**
 * Author:Khaos
 * Date:2020/8/12
 * Time:13:56
 */
class SplashActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //一个小时变一张图
  private val randomImg = TimeUtils.millis2String(System.currentTimeMillis())
      .split(" ")[1].split(":")[0].toInt()

  //是否需要关闭页面
  private var hasFinish = false

  //是否有SD卡权限，只有
  private var hasSDPermission = false

  //倒计时是否结束
  private var countDownFinish = false
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override fun layoutResId() = R.layout.activity_splash
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="状态栏设置">
  override fun initStatus() {
    immersionBar {
      fullScreen(true)
      navigationBarColor(R.color.transparent)
      statusBarDarkFont(true)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化View">
  override fun initView() {
    hasFinish = checkReOpenHome()
    if (hasFinish) return
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_MUTE, 0
    ) //静音
    splashTv.click {
      splashSVGA?.callback = null
      countDownFinish = true
      splashTv.gone()
      goNextPage()
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化数据">
  override fun initData() {
    if (hasFinish) return
    //随机加载图片
    //splashIv.loadCacheFileFullScreen(ImageUrls.instance.getRandomImgUrl(randomImg))
    splashSVGA.callback = object : SVGACallback {
      override fun onFinished() {
        countDownFinish = true
        splashTv.gone()
        goNextPage()
      }

      override fun onPause() {}
      override fun onRepeat() {}
      override fun onStep(frame: Int, percentage: Double) {}
    }
    disposable = Observable.timer(2, TimeUnit.SECONDS)
        .compose(RxUtils.instance.rx2SchedulerHelperO())
        .subscribe { splashTv.visible() }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="第一次打开触发">
  private var isFirstOnResume = true
  override fun onResume() {
    super.onResume()
    if (isFirstOnResume) {
      isFirstOnResume = false
      hasSDPermission = PermissionUtils.instance.hasSDPermission()
      //请求SD卡权限
      if (!hasSDPermission) {
        XXPermissions.with(this)
            .permission(Permission.MANAGE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
              override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                if (PermissionUtils.instance.hasSDPermission()) {
                  hasSDPermission = true
                  goNextPage()
                } else {
                  StringUtils.getString(R.string.permission_sdcard).toast()
                  finish()
                }
              }

              override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                if (never) {
                  StringUtils.getString(R.string.permission_sdcard_never).toast()
                  // 如果是被永久拒绝就跳转到应用权限系统设置页面
                  XXPermissions.startPermissionActivity(mActivity, permissions)
                } else {
                  StringUtils.getString(R.string.permission_sdcard_must).toast()
                }
                finish()
              }
            })
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="防止重新打开">
  //https://www.cnblogs.com/xqz0618/p/thistaskroot.html
  private fun checkReOpenHome(): Boolean {
    // 避免从桌面启动程序后，会重新实例化入口类的activity
    if (!this.isTaskRoot && intent != null // 判断当前activity是不是所在任务栈的根
        && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
        && Intent.ACTION_MAIN == intent.action
    ) {
      finish()
      return true
    }
    return false
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="打开下一个页面">
  private fun goNextPage() {
    if (!hasSDPermission) return
    if (!countDownFinish) return
    if (MMkvUtils.instance.getShowGuide()) {
      GuideActivity.startActivity(mContext)
    } else {
      MainActivity.startActivity(mActivity)
    }
    finish()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="销毁">
  //禁止返回
  override fun onBackPressed() {
  }

  private var disposable: Disposable? = null
  override fun finish() {
    super.finish()
    disposable?.dispose()
    splashSVGA?.clear()
    splashSVGA?.callback = null
    (getSystemService(Context.AUDIO_SERVICE) as AudioManager).adjustStreamVolume(
        AudioManager.STREAM_MUSIC,
        AudioManager.ADJUST_UNMUTE, 0
    ) //非静音
  }
  //</editor-fold>
}