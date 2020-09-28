package com.cc.base2021.utils

import androidx.annotation.IntRange
import com.cc.base2021.bean.local.VideoBean

/**
 * Description:
 * @author: caiyoufei
 * @date: 2019/12/12 11:49
 */
class VideoRandomUtils private constructor() {
  private object SingletonHolder {
    val holder = VideoRandomUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  //视频资源封面和播放地址
  private var resourceList = mutableListOf(
      //普通播放地址
      //Pair(
      //    "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4",
      //    "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
      //),
      //Pair(
      //    "https://www.w3schools.com/html/movie.mp4",
      //    "https://www.w3schools.com/html/movie.mp4"
      //),
      //Pair(
      //    "https://media.w3.org/2010/05/sintel/trailer.mp4",
      //    "https://media.w3.org/2010/05/sintel/trailer.mp4"
      //),
      //电影预告片 https://www.yugaopian.cn/
      Pair(
          "https://vod.pipi.cn/43903a81vodtransgzp1251246104/bbd4f07a5285890808066187974/v.f42906.mp4",
          "https://vod.pipi.cn/43903a81vodtransgzp1251246104/bbd4f07a5285890808066187974/v.f42906.mp4"
      ),
      Pair(
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/ff5db9495285890807841001288/v.f42906.mp4",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/ff5db9495285890807841001288/v.f42906.mp4"
      ),
      Pair(
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/ccff07ce5285890807898977876/v.f42906.mp4",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/ccff07ce5285890807898977876/v.f42906.mp4"
      ),
      Pair(
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/09035e1b5285890807960446399/v.f42906.mp4",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/09035e1b5285890807960446399/v.f42906.mp4"
      ),
      Pair(
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/87d0caf85285890807055577675/v.f42906.mp4",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/87d0caf85285890807055577675/v.f42906.mp4"
      ),
      Pair(
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/6be0b3615285890808105900224/v.f42906.mp4",
          "https://vod.pipi.cn/fec9203cvodtransbj1251246104/6be0b3615285890808105900224/v.f42906.mp4"
      ),
      //直播地址 https://blog.csdn.net/XiaoYuWen1242466468/article/details/90287886
      Pair(
          "rtmp://202.69.69.180:443/webcast/bshdlive-pc",
          "rtmp://202.69.69.180:443/webcast/bshdlive-pc" //香港财经
      ),
      Pair(
          "rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp",
          "rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp" //韩国GoodTV
      ),
      //Pair(
      //    "rtmp://ns8.indexforce.com/home/mystream", //美国1
      //    "rtmp://ns8.indexforce.com/home/mystream"
      //),
      //Pair(
      //    "rtmp://media3.scctv.net/live/scctv_800", //美国2
      //    "rtmp://media3.scctv.net/live/scctv_800"
      //),
      //Pair(
      //    "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", //动漫
      //    "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov"
      //)
  )

  //获取随机视频数量
  fun getVideoList(
      @IntRange(from = 0) idStart: Long = 0, @IntRange(from = 1, to = 100) count: Int = 10
  ): MutableList<VideoBean> {
    val result = mutableListOf<VideoBean>()
    for (i in 0 until count) {
      val pair = resourceList[(i + idStart).toInt() % resourceList.size]
      result.add(
          VideoBean(
              id = idStart + i,
              thumb = pair.first,
              url = pair.second,
              title = "这是第${i + idStart}个视频"
          )
      )
    }
    return result
  }

  //获取视频信息
  fun getVideoPair(index: Int): Pair<String, String> {
    return resourceList[index % resourceList.size]
  }

  //随机生成一个视频
  fun randomVideo(): Pair<String, String> {
    return resourceList[((Math.random() * resourceList.size).toInt()) % resourceList.size]
  }
}