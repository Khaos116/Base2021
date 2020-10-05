package com.cc.music.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cc.music.enu.PlayController

/**
 * Author:CASE
 * Date:2020-10-5
 * Time:19:24
 */
class IntentActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    sendBroadcast(Intent().apply { action = PlayController.DETAIL.name })
    finish()
  }
}