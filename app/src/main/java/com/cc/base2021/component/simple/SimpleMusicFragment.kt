package com.cc.base2021.component.simple

import com.cc.base2021.R
import com.cc.base2021.comm.CommFragment
import com.cc.base2021.ext.loadImgSquare
import com.cc.base2021.utils.MusicRandomUtils
import com.cc.ext.logE
import kotlinx.android.synthetic.main.fragment_simple_music.musicView

/**
 * Author:CASE
 * Date:2020-10-1
 * Time:15:47
 */
class SimpleMusicFragment : CommFragment() {
  //<editor-fold defaultstate="collapsed" desc="外部获取实例">
  companion object {
    fun newInstance(): SimpleMusicFragment {
      val fragment = SimpleMusicFragment()
      return fragment
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="XML">
  override val contentXmlId = R.layout.fragment_simple_music
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化">
  override fun lazyInit() {
    musicView.callLoadCover = { url, iv -> iv.loadImgSquare(url) }
    musicView.setMusicList(MusicRandomUtils.instance.musicList)
    musicView.startMusic()
  }
  //</editor-fold>
}