package com.cc.music;

import com.cc.music.bean.MusicBean;
import com.cc.music.IMusicCall;

interface IMusicOperate {
  void setPlayLoop(boolean loop);

  void setPlayAuto(boolean auto);

  void setPlayMode(String mode);

  void setPlayList(in List<MusicBean> list);

  void addPlayList(in List<MusicBean> list);

  void playStartByIndex(int index);

  void playStartByUid(String uid);

  void playStart();

  void playPause();

  void playStop();

  void playRelease();

  void playPrevious();

  void playNext();

  void playSeekTo(long msc);

  void registerCallback(IMusicCall callback);

  void unRegisterCallback(IMusicCall callback);

  String getPlayMode();

  String getPlayState();

  long getPlayProgress();

  long getBufferProgress();

  long getDuration();

  String getMusic();
}