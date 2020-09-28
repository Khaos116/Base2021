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
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/21/mp4/190321153853126488.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319222227698228.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319212559089721.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318231014076505.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/13/mp4/190313094901111138.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/eb411c2810f04ffa8aaafc42052b233820180418095416.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4"
      ),
      Pair(
          "https://cms-bucket.nosdn.127.net/cb37178af1584c1588f4a01e5ecf323120180418133127.jpeg",
          "http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4"
      ),
      //https://blog.csdn.net/XiaoYuWen1242466468/article/details/90287886
      Pair(
          "rtmp://202.69.69.180:443/webcast/bshdlive-pc",
          "rtmp://202.69.69.180:443/webcast/bshdlive-pc"
      ),
      Pair(
          "rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp",
          "rtmp://mobliestream.c3tv.com:554/live/goodtv.sdp"
      ),
      Pair(
          "rtmp://ns8.indexforce.com/home/mystream",
          "rtmp://ns8.indexforce.com/home/mystream"
      ),
      Pair(
          "rtmp://media3.scctv.net/live/scctv_800",
          "rtmp://media3.scctv.net/live/scctv_800"
      )
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