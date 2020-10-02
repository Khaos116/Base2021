package com.cc.music;

interface IMusicCall {
  void callMusicInfo(String musicJson);

  void callPlayMode(String mode);

  void callPlayState(String state);

  void callPlayDuration(long duration);

  void callPlayProgress(long progress);

  void callBufferProgress(long progress);

  void callBufferPercent(int percent, float kbps);
}