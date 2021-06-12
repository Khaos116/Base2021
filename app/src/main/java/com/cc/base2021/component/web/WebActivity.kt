package com.cc.base2021.component.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.http.SslError
import android.view.*
import android.webkit.*
import com.blankj.utilcode.util.ColorUtils
import com.cc.base2021.R
import com.cc.base2021.comm.CommTitleActivity
import com.cc.base2021.config.HeaderManger
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import kotlinx.android.synthetic.main.activity_web.webRootView

/**
 * Description: 如果需要js对接，参考添加BridgeWebView https://github.com/lzyzsd/JsBridge
 * Author:Khaos
 * Date:2020/8/21
 * Time:9:34
 */
class WebActivity : CommTitleActivity() {
  //<editor-fold defaultstate="collapsed" desc="外部跳转">
  //外部跳转
  companion object {
    private const val WEB_URL = "INTENT_KEY_WEB_URL"
    fun startActivity(context: Context, url: String) {
      val intent = Intent(context, WebActivity::class.java)
      //"https://chatlink.mstatik.com/widget/standalone.html?eid=125038"
      if (url.isNotBlank()) intent.putExtra(WEB_URL, url)
      context.startActivity(intent)
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  //需要加载的web地址
  private var webUrl: String? = null

  //AgentWeb相关
  private var agentWeb: AgentWeb? = null
  private var agentBuilder: AgentWeb.CommonBuilder? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  //xml
  override fun layoutResContentId() = R.layout.activity_web
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化view">
  //初始化view
  override fun initContentView() {
    webRootView.removeAllViews()
    initAgentBuilder()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化数据">
  //加载数据
  @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
  override fun initData() {
    webUrl = intent.getStringExtra(WEB_URL) ?: "https://www.baidu.com" //获取加载地址
    agentWeb = agentBuilder?.createAgentWeb()?.ready()?.go(webUrl) //创建web并打开
    //设置适配
    val web = agentWeb?.webCreator?.webView
    web?.settings?.let { ws ->
      //支持javascript
      ws.javaScriptEnabled = true
      //设置可以支持缩放
      ws.setSupportZoom(true)
      //设置内置的缩放控件
      ws.builtInZoomControls = true
      //隐藏原生的缩放控件
      ws.displayZoomControls = false
      //扩大比例的缩放
      ws.useWideViewPort = true
      //自适应屏幕
      ws.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
      ws.loadWithOverviewMode = true
    }
    //解决键盘不弹起的BUG
    web?.requestFocus(View.FOCUS_DOWN)
    web?.setOnTouchListener { v, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> if (!v.hasFocus()) {
          v.requestFocus()
        }
      }
      false
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化AgentWeb">
  //初始化web
  private fun initAgentBuilder() {
    //为了解决安卓5.x的bug
    val webView = WebView(this)
    webView.overScrollMode = View.OVER_SCROLL_NEVER
    webView.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
    agentBuilder = AgentWeb.with(this)
      .setAgentWebParent(webRootView, ViewGroup.LayoutParams(-1, -1)) //添加到父容器
      .useDefaultIndicator(ColorUtils.getColor(R.color.colorPrimary)) //设置进度条颜色
      //.setWebViewClient(getWebViewClient()) //监听结束，适配宽度
      .setWebViewClient(getWebViewClientSSL()) //SSL
      .setWebChromeClient(webChromeClient) //监听标题
      .setWebView(webView) //真正的webview
      .setMainFrameErrorView(R.layout.agentweb_error_page, -1) //失败的布局
      .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
      .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
      .interceptUnkownUrl() //拦截找不到相关页面的Scheme
    //给WebView添加Header
    val headers = HeaderManger.instance.getStaticHeaders()
    agentBuilder?.additionalHttpHeader(webUrl, headers)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="获取标题Web标题">
  //获取标题
  private val webChromeClient = object : com.just.agentweb.WebChromeClient() {

    override fun onReceivedTitle(view: WebView?, title: String?) {
      super.onReceivedTitle(view, title)
      title?.let { setTitleText(it) }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="解决SSL无法打开(但是可能存在安全隐患)">
  //解决SSL无法打开的问题
  private fun getWebViewClientSSL(): com.just.agentweb.WebViewClient {
    return object : com.just.agentweb.WebViewClient() {
      override fun onReceivedSslError(
        view: WebView?,
        handler: SslErrorHandler,
        error: SslError?
      ) {
        handler.proceed()
      }
    }
  }
  //</editor-fold>
}