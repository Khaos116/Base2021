package com.cc.music.bean

import android.os.Parcelable
import com.blankj.utilcode.util.GsonUtils
import kotlinx.android.parcel.Parcelize

/**
 * Author:Khaos
 * Date:2020-10-1
 * Time:12:39
 */
@Parcelize
data class MusicBean(
    val url: String,
    val songName: String? = "",
    val singerName: String? = "",
    val songCover: String? = ""
) : Parcelable {
  fun getUid(): String {
    return GsonUtils.toJson(this)
  }
}