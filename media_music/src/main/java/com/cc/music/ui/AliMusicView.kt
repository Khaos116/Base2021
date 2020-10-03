package com.cc.music.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleObserver
import com.blankj.utilcode.util.*
import com.cc.ext.*
import com.cc.music.IMusicCall
import com.cc.music.R
import com.cc.music.bean.MusicBean
import com.cc.music.enu.PlayMode
import com.cc.music.enu.PlayState
import com.cc.music.startup.mMusicPlayerService
import com.cc.music.utils.MusicTimeUtils
import kotlinx.android.synthetic.main.layout_ali_music.view.*
import kotlinx.coroutines.*

/**
 * http://music.163.com/song/media/outer/url?id=562598065.mp3
 * http://music.163.com/song/media/outer/url?id=1431865160.mp3
 * Author:CASE
 * Date:2020-10-1
 * Time:15:51
 */
class AliMusicView @JvmOverloads constructor(
    private val con: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(con, attrs, defStyleAttr, defStyleRes), LifecycleObserver {
  //<editor-fold defaultstate="collapsed" desc="变量">
  //是否正在拖动进度
  private var isSeeking = false

  //当前播放状态
  private var mPlayState = PlayState.SET_DATA

  //当前播放模式
  private var mPlayMode = PlayMode.LOOP_ALL

  //封面加载回调
  var callLoadCover: ((url: String?, iv: ImageView) -> Unit)? = null
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="初始化XML">
  init {
    LayoutInflater.from(con).inflate(R.layout.layout_ali_music, this, true)
    music_controller_play_pause.pressEffectAlpha(0.9f)
    music_controller_stop.pressEffectAlpha(0.9f)
    music_controller_previous.pressEffectAlpha(0.9f)
    music_controller_next.pressEffectAlpha(0.9f)
    music_controller_play_mode.pressEffectAlpha(0.9f)
    music_controller_stop.click { stopMusic() }
    music_controller_previous.click { playPrevious() }
    music_controller_next.click { playNext() }
    music_controller_play_pause.click {
      when (mPlayState) {
        PlayState.START, PlayState.BUFFED, PlayState.SEEKED -> pauseMusic()
        PlayState.PAUSE, PlayState.STOP, PlayState.COMPLETE, PlayState.ERROR -> startMusic()
        else -> startMusic()
      }
    }
    music_controller_play_mode.click {
      val newMode = when (mPlayMode) {
        PlayMode.LOOP_ALL -> PlayMode.LOOP_ONE
        PlayMode.LOOP_ONE -> PlayMode.PLAY_IN_ORDER
        PlayMode.PLAY_IN_ORDER -> PlayMode.PLAY_RANDOM
        PlayMode.PLAY_RANDOM -> PlayMode.LOOP_ALL
      }
      callPlayModeMusic(newMode.name)
      setModeMusic(newMode)
    }
    music_controller_seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser && music_controller_seekbar.max > 0) {
          isSeeking = true
          music_controller_time.text = MusicTimeUtils.instance.forMatterMusicTime(progress.toLong() * 1000)
        }
      }

      override fun onStartTrackingTouch(seekBar: SeekBar) {}

      override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (isSeeking) {
          seekToMusic(seekBar.progress.toLong() * 1000)
          isSeeking = false
        }
      }
    })
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="内部调用">
  //更新播放状态
  private fun callPlayStateMusic(state: String) {
    GlobalScope.launch(Dispatchers.Main) {
      mPlayState = PlayState.valueOf(state)
      when (mPlayState) {
        PlayState.PAUSE -> music_controller_play_pause.isSelected = true
        PlayState.ERROR -> {
          music_controller_play_pause.isSelected = true
          checkNoNet()
        }
        PlayState.STOP, PlayState.COMPLETE -> {
          music_controller_seekbar.secondaryProgress = 0
          music_controller_seekbar.progress = 0
          music_controller_time.text = MusicTimeUtils.instance.forMatterMusicTime(0)
          music_controller_play_pause.isSelected = true
        }
        else -> music_controller_play_pause.isSelected = false
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun checkNoNet() {
    if (ContextCompat.checkSelfPermission(Utils.getApp(),
            Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED && !NetworkUtils.isConnected()) {
      StringUtils.getString(R.string.play_music_error_net).toast()
    }
  }

  //更新播放模式
  private fun callPlayModeMusic(mode: String) {
    GlobalScope.launch(Dispatchers.Main) {
      mPlayMode = PlayMode.valueOf(mode)
      music_controller_play_mode.setImageResource(
          when (mPlayMode) {
            PlayMode.PLAY_RANDOM -> R.drawable.svg_media_random
            PlayMode.PLAY_IN_ORDER -> R.drawable.svg_media_in_order
            PlayMode.LOOP_ALL -> R.drawable.svg_media_loop_all
            PlayMode.LOOP_ONE -> R.drawable.svg_media_loop_one
          }
      )
    }
  }

  private fun fillSongInfo(json: String?) {
    GlobalScope.launch(Dispatchers.Main) {
      json?.let {
        val music = GsonUtils.fromJson<MusicBean>(it, MusicBean::class.java)
        music_song_name.text = music.songName
        music_singer_name.text = music.singerName
        callLoadCover?.invoke(music?.songCover, music_song_cover)
      }
    }
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="生命周期">
  override fun onAttachedToWindow() {
    super.onAttachedToWindow()
    Utils.getApp().mMusicPlayerService?.let {
      fillSongInfo(it.music)
      //注册回调
      it.registerCallback(callBackMusic)
      //获取播放模式
      callPlayModeMusic(it.playMode)
      //获取播放状态
      callPlayStateMusic(it.playState)
      //获取总时长
      val duration = if (mPlayState == PlayState.SET_DATA) 0L else it.duration
      music_controller_seekbar.max = duration.toInt() / 1000
      music_controller_duration.text = MusicTimeUtils.instance.forMatterMusicTime(duration)
      //获取当前播放进度
      val playProgress = if (mPlayState == PlayState.SET_DATA) 0L else it.playProgress
      music_controller_seekbar.progress = playProgress.toInt() / 1000
      music_controller_time.text = MusicTimeUtils.instance.forMatterMusicTime(playProgress)
      //获取当前缓冲进度
      val bufferProgress = if (mPlayState == PlayState.SET_DATA) 0L else it.bufferProgress
      music_controller_seekbar.secondaryProgress = bufferProgress.toInt() / 1000
    }
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    Utils.getApp().mMusicPlayerService?.unRegisterCallback(callBackMusic)
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="外部调用">
  fun addMusicList(list: MutableList<MusicBean>) {
    Utils.getApp().mMusicPlayerService?.addPlayList(list)
  }

  fun setMusicList(list: MutableList<MusicBean>) {
    Utils.getApp().mMusicPlayerService?.setPlayList(list)
  }

  fun setModeMusic(mode: PlayMode) {
    Utils.getApp().mMusicPlayerService?.playMode = mode.name
  }

  fun setAutoPlayMusic(auto: Boolean) {
    Utils.getApp().mMusicPlayerService?.setPlayAuto(auto)
  }

  fun startMusicUid(music: MusicBean) {
    Utils.getApp().mMusicPlayerService?.playStartByUid(music.getUid())
  }

  fun startMusicIndex(index: Int = 0) {
    Utils.getApp().mMusicPlayerService?.playStartByIndex(index)
  }

  fun startMusicUid(uid: String) {
    Utils.getApp().mMusicPlayerService?.playStartByUid(uid)
  }

  fun startMusic() {
    Utils.getApp().mMusicPlayerService?.playStart()
  }

  fun pauseMusic() {
    Utils.getApp().mMusicPlayerService?.playPause()
  }

  fun stopMusic() {
    Utils.getApp().mMusicPlayerService?.playStop()
  }

  fun seekToMusic(msc: Long) {
    Utils.getApp().mMusicPlayerService?.playSeekTo(msc)
  }

  fun playNext() {
    Utils.getApp().mMusicPlayerService?.playNext()
  }

  fun playPrevious() {
    Utils.getApp().mMusicPlayerService?.playPrevious()
  }
  //</editor-fold>

  //<editor-fold defaultstate="collapsed" desc="回调监听">
  private var callBackMusic = object : IMusicCall.Stub() {
    override fun callMusicInfo(musicJson: String?) {
      GlobalScope.launch(Dispatchers.Main) {
        fillSongInfo(musicJson)
      }
    }

    override fun callPlayMode(mode: String) {
      GlobalScope.launch(Dispatchers.Main) {
        callPlayModeMusic(mode)
      }
    }

    override fun callPlayState(state: String) {
      GlobalScope.launch(Dispatchers.Main) {
        callPlayStateMusic(state)
      }
    }

    override fun callPlayDuration(duration: Long) {
      GlobalScope.launch(Dispatchers.Main) {
        music_controller_seekbar.max = duration.toInt() / 1000
        music_controller_duration.text = MusicTimeUtils.instance.forMatterMusicTime(duration)
      }
    }

    override fun callPlayProgress(progress: Long) {
      if (!isSeeking) {
        GlobalScope.launch(Dispatchers.Main) {
          music_controller_seekbar.progress = progress.toInt() / 1000
          music_controller_time.text = MusicTimeUtils.instance.forMatterMusicTime(progress)
        }
      }
    }

    override fun callBufferProgress(progress: Long) {
      GlobalScope.launch(Dispatchers.Main) {
        music_controller_seekbar.secondaryProgress = progress.toInt() / 1000
      }
    }

    override fun callBufferPercent(percent: Int, kbps: Float) {
    }
  }
  //</editor-fold>
}