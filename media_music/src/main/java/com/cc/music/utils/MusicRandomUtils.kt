package com.cc.music.utils

import com.cc.music.bean.MusicBean

/**
 * Author:Khaos
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
      //=========================================我的歌单=========================================//
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=533259686.mp3",
          songName = "BINGBIAN病变",
          singerName = "Cubi/Fi9江澈/Birck",
          songCover = "http://p1.music.126.net/3wSMVTdxeH2wN02yTxvhvw==/109951164388312861.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1342990048.mp3",
          songName = "一百个不喜欢你的方法",
          singerName = "房东的猫",
          songCover = "http://p1.music.126.net/ER-HeXRkicviudNEuih-tA==/109951163827799249.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=437752889.mp3",
          songName = "不要丢下我",
          singerName = "六哲",
          songCover = "http://p2.music.126.net/DwfPeVT2MppOhr9ApULS_Q==/18195817928539895.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=496314770.mp3",
          songName = "不值得爱吗",
          singerName = "金雨",
          songCover = "http://p1.music.126.net/roEg-lZxrlLixMliF3qEZQ==/109951163018616271.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=546715085.mp3",
          songName = "错爱",
          singerName = "季彦霖/刘增瞳",
          songCover = "http://p1.music.126.net/i_JxdIZg-Wto0EthYtBt0A==/109951163198809349.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=554191776.mp3",
          songName = "只是配角",
          singerName = "伊晗",
          songCover = "http://p1.music.126.net/PY8L4dO2apPK8cS1WcwuCw==/109951163263390697.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=535793433.mp3",
          songName = "最后我们没在一起",
          singerName = "白小白",
          songCover = "http://p1.music.126.net/kP_CwTTxzdTCzjq08lFVOw==/109951163134484171.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=30814948.mp3",
          songName = "斑马斑马",
          singerName = "房东的猫",
          songCover = "http://p1.music.126.net/SpOw9nHtbBKnNhUYKevOaw==/7880199836658654.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=437752886.mp3",
          songName = "累了走了散了",
          singerName = "六哲",
          songCover = "http://p1.music.126.net/DwfPeVT2MppOhr9ApULS_Q==/18195817928539895.jpg",
      ),
      //=========================================排行榜歌单=========================================//
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1436709403.mp3",
          songName = "夏天的风",
          singerName = "羊瞌睡了",
          songCover = "http://p1.music.126.net/rFUKVdOjqxgwAT6Zi6qv7A==/109951164906689206.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1374154676.mp3",
          songName = "无期",
          singerName = "光头华夏",
          songCover = "http://p1.music.126.net/kFFj2ZXHP_cI_RV8Et7feA==/109951164630675384.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1386259535.mp3",
          songName = "飞",
          singerName = "恩信Est/二胖u",
          songCover = "http://p1.music.126.net/_5I2VNMes4k4lh5RyKI50A==/109951164532205791.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=467899061.mp3",
          songName = "春风十里",
          singerName = "旧港",
          songCover = "http://p2.music.126.net/vkewaQi0kB0JOf1GSZHMKg==/109951162884866968.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1400256289.mp3",
          songName = "你的答案",
          singerName = "阿冗",
          songCover = "http://p2.music.126.net/OlX-4S4L0Hdkyy_DQ27zag==/109951164459621658.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1363948882.mp3",
          songName = "世间美好与你环环相扣",
          singerName = "柏松",
          songCover = "http://p2.music.126.net/DK1_4sP_339o5rowMdPXdw==/109951164071024476.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1421613404.mp3",
          songName = "期待风雨后更美的彩虹",
          singerName = "密斯特黄",
          songCover = "http://p2.music.126.net/jItAymh2s4hqeHMYDmcpOw==/109951164685274888.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1403318151.mp3",
          songName = "把回忆拼好给你",
          singerName = "王贰浪",
          songCover = "http://p2.music.126.net/CBx2K_jEN3SNWwYztagPPw==/109951164485969446.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1458462128.mp3",
          songName = "跨不过的距离",
          singerName = "虎二",
          songCover = "http://p2.music.126.net/6mKxPiFn2_Df6JUldwCeUw==/109951165093090252.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1428520173.mp3",
          songName = "忘了",
          singerName = "江皓南",
          songCover = "http://p2.music.126.net/_FXRdfYM2GD2Cb2_1LQlfA==/109951164772228922.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1403522555.mp3",
          songName = "和你一起每一天",
          singerName = "郑媛尹",
          songCover = "http://p2.music.126.net/Cfh86oxbW3imIM-qdsElzA==/109951164487666878.jpg",
      ),
      MusicBean(
          url = "http://music.163.com/song/media/outer/url?id=1404654205.mp3",
          songName = "孤单不可怕",
          singerName = "牙仙子",
          songCover = "http://p1.music.126.net/YK-9ZCt3skOJH1vJ3-Z0UA==/109951164497754300.jpg",
      ),
  )
}