package com.cc.base2021.ext

import android.widget.ImageView
import coil.api.load
import coil.transform.CircleCropTransformation
import coil.transition.Transition
import com.cc.base2021.R

/**
 * Author:case
 * Date:2020/8/12
 * Time:18:28
 */
//普通加载
fun ImageView.load(url: String?) {
  if (url.isNullOrBlank()) {
    this.setImageResource(R.drawable.error_square)
  } else {
    this.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_square)
      error(R.drawable.error_square)
    }
  }
}

//全屏加载
fun ImageView.loadFullScreen(url: String?) {
  if (url.isNullOrBlank()) {
    this.setImageResource(R.drawable.error_720p)
  } else {
    this.load(url) {
      crossfade(true)
      placeholder(R.drawable.loading_720p)
      error(R.drawable.error_720p)
    }
  }
}