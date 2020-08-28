package com.cc.base2021.component.splash

import com.blankj.utilcode.util.TimeUtils
import com.cc.base.ext.*
import com.cc.base.utils.PermissionUtils
import com.cc.base2021.R
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.component.guide.GuideActivity
import com.cc.base2021.component.main.MainActivity
import com.cc.base2021.constants.ImageUrls
import com.cc.base2021.ext.loadCacheFileFullScreen
import com.cc.base2021.utils.MMkvUtils
import com.cc.base2021.utils.RxUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.permissions.*
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.splashIv
import kotlinx.android.synthetic.main.activity_splash.splashTv
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Author:case
 * Date:2020/8/12
 * Time:13:56
 */
class SplashActivity : CommActivity() {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //一个小时变一张图
  private val randomImg = TimeUtils.millis2String(System.currentTimeMillis())
    .split(" ")[1].split(":")[0].toInt()

  //倒计时
  private var countTime = 3L

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
    //UI显示出来再执行倒计时和权限判断
    mContentView.post {
      hasSDPermission = PermissionUtils.instance.hasSDPermission()
      //倒计时
      countDown()
      //请求SD卡权限
      if (!hasSDPermission) {
        XXPermissions.with(this)
          .permission(Permission.MANAGE_EXTERNAL_STORAGE)
          .request(object : OnPermission {
            override fun hasPermission(granted: MutableList<String>, all: Boolean) {
              if (PermissionUtils.instance.hasSDPermission()) {
                hasSDPermission = true
                goNextPage()
              } else {
                "必须要给予SD卡权限才能使用".toast()
                finish()
              }
            }

            override fun noPermission(denied: MutableList<String>, quick: Boolean) {
              if (quick) {
                "被永久拒绝授权，请手动授予存储权限".toast()
                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                XXPermissions.startPermissionActivity(mActivity, denied);
              } else {
                "必须要给予SD卡权限才能使用".toast()
              }
              finish()
            }
          })
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化数据">
  override fun initData() {
    //随机加载图片
    splashIv.loadCacheFileFullScreen(ImageUrls.instance.getRandomImgUrl(randomImg))
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="倒计时">
  private fun countDown() {
    disposable = Flowable.intervalRange(0, countTime + 1, 0, 1, TimeUnit.SECONDS)
      .compose(RxUtils.instance.rx2SchedulerHelperF())
      .doOnNext { splashTv.text = String.format("%d", max(1, countTime - it)) }
      .doOnComplete {
        countDownFinish = true
        splashTv.gone()
        goNextPage()
      }
      .subscribe()
  }

  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="打开下一个页面">
  private fun goNextPage() {
    if (!hasSDPermission) return
    if (!countDownFinish) return
    if (MMkvUtils.instance.getShowGuide()) {
      GuideActivity.startActivity(mContext)
      finish()
    } else {
      MainActivity.startActivity(mActivity)
    }
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
  }
  //</editor-fold>
}