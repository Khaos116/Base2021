package com.cc.base2021.utils

import com.cc.music.bean.MusicBean

/**
 * Author:CASE
 * Date:2020-10-2
 * Time:15:13
 */
class MusicRandomUtils private constructor() {
  private object SingletonHolder {
    val holder = MusicRandomUtils()
  }

  companion object {
    val instance = SingletonHolder.holder
  }

  var musicList = mutableListOf(
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1421613404.mp3",
          songName = "期待风雨后更美的彩虹",
          songCover = "http://p2.music.126.net/jItAymh2s4hqeHMYDmcpOw==/109951164685274888.jpg",
          singerName = "密斯特黄"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1348896822.mp3",
          songName = "所爱隔山海",
          songCover = "http://p2.music.126.net/MOmuZfdM4aUBgleLUDevoA==/109951164269620044.jpg",
          singerName = "CMJ"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1403318151.mp3",
          songName = "把回忆拼好给你",
          songCover = "http://p2.music.126.net/CBx2K_jEN3SNWwYztagPPw==/109951164485969446.jpg",
          singerName = "王贰浪"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1458462128.mp3",
          songName = "跨不过的距离",
          songCover = "http://p2.music.126.net/6mKxPiFn2_Df6JUldwCeUw==/109951165093090252.jpg",
          singerName = "虎二"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1428520173.mp3",
          songName = "忘了",
          songCover = "http://p2.music.126.net/_FXRdfYM2GD2Cb2_1LQlfA==/109951164772228922.jpg",
          singerName = "江皓南"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1403522555.mp3",
          songName = "和你一起每一天",
          songCover = "http://p2.music.126.net/Cfh86oxbW3imIM-qdsElzA==/109951164487666878.jpg",
          singerName = "郑媛尹"
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1404654205.mp3",
          songName = "孤单不可怕",
          songCover = "http://p1.music.126.net/YK-9ZCt3skOJH1vJ3-Z0UA==/109951164497754300.jpg",
          singerName = "牙仙子"
      ),
  )
}