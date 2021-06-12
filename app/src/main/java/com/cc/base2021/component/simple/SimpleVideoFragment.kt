package com.cc.base2021.component.simple

import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.ext.loadNetVideoCover
import com.cc.base2021.utils.VideoRandomUtils
import com.cc.ext.isLiveUrl
import com.cc.video.utils.VideoOverUtils
import kotlinx.android.synthetic.main.fragment_simple_video.simpleVideoView

/**
 * Author:Khaos
 * Date:2020-9-16
 * Time:17:00
 */
class SimpleVideoFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(url: String = "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"): SimpleVideoFragment {
      val fragment = SimpleVideoFragment()
      fragment.mUrl = url
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="变量">
  private var mUrl: String = ""
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_simple_video
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    //视频数据
    val videoBean = VideoRandomUtils.instance.randomVideo()
    //视频控制器
    VideoOverUtils.instance.run {
      if (videoBean.second.isLiveUrl()) useLiveController(simpleVideoView) { url, iv -> iv.loadNetVideoCover(url) }
      else useStandardController(simpleVideoView) { url, iv -> iv.loadNetVideoCover(url) }
    }
    simpleVideoView.setUrlVideo(
        url = videoBean.second,
        cover = videoBean.second,
        title = videoBean.first
    )
  }
  //</editor-fold>
}