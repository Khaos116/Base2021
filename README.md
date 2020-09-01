# Base2021(AndroidX + MVVM + LiveData + Kotlin协程)，在[Abase](https://github.com/caiyoufei/ABase)的基础上进一步优化  

新的三方库|描述  
:------------------------------------:|:------------------------------------:
**[startup](https://developer.android.google.cn/topic/libraries/app-startup)**|安卓系统启动优化
**[freeProGuard](https://developer.android.google.cn/topic/libraries/app-startup)**|防止混淆
**[xxpermissions](https://github.com/getActivity/XXPermissions)**|动态权限请求(已适配安卓11)
**[coil](https://github.com/coil-kt/coil)**|Kotlin版本的图片加载(目前还有坑)
**[multiType](https://github.com/drakeet/MultiType)**|多类型适配器(和Epoxy比较像)
**[svga](https://github.com/svga/SVGAPlayer-Android/blob/master/readme.zh.md)**|比Lottie更省内存的动画  
======================================|======================================

旧的三方库|描述  
:------------------------------------:|:------------------------------------:
**[utilcodex](https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/README-CN.md)**|安卓强大的三方工具库
**[autosize](https://github.com/JessYanCoding/AndroidAutoSize)**|安卓屏幕适配
**[timber](https://github.com/JakeWharton/timber)**|Log打印
**[immersionbar](https://github.com/gyf-dev/ImmersionBar)**|沉浸式适配
**[mmkv](https://github.com/Tencent/MMKV)**|SharedPreferences替代品
**[rxhttp](https://github.com/liujingxing/okhttp-RxHttp)**|自带缓存和Kotlin协程的网络请求
**[eventBus](https://github.com/JeremyLiao/LiveEventBus)**|LiveData实现的EventBus
**[agentweb](https://github.com/Justson/AgentWeb)**|易于使用的WebView封装
**[swipe](https://github.com/luckybilly/SmartSwipe)**|集侧滑和下拉刷新等功能为一体
**[cictureSelector](https://github.com/LuckSiege/PictureSelector)**|多媒体选择和预览
======================================|======================================

相关工具|描述  
:------------------------------------:|:------------------------------------:
**[Dependencies](https://github.com/caiyoufei/Base2021/blob/master/buildSrc/src/main/java/Dependencies.kt)**|将三方依赖统一管理
**[DiscreteScrollView](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/discretescrollview/DiscreteScrollView.java)**|解决多指无限滑动Page问题
**[EpoxyItemDecoration](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/decoration/EpoxyItemDecoration.kt)**|解决RecyclerView烦恼的分割线
**[MyItemTouchHelperCallback](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/drag/MyItemTouchHelperCallback.java)**|解决Grid模式的RecyclerView拖拽排序问题
**[StickyHeaderLinearLayoutManager](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/sticky/StickyHeaderLinearLayoutManager.kt)**|解决RecyclerView烦恼的Sticky悬浮效果
**[SimpleViewpagerIndicator](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/SimpleViewpagerIndicator.java)**|简单的ViewPager标题适配
**[FlashingTextView](https://github.com/caiyoufei/Base2021/blob/master/app/src/main/java/com/cc/base2021/widget/FlashingTextView.java)**|闪动的TextView  
======================================|======================================