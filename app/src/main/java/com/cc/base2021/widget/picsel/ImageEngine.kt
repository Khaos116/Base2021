package com.cc.base2021.widget.picsel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.clear
import coil.imageLoader
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.cc.base2021.ext.loadImgSquare
import com.cc.base2021.ext.loadImgVerticalScreen
import com.cc.base2021.utils.PlaceHolderUtils
import com.cc.ext.gone
import com.cc.ext.visible
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.*

/**
 * Author:case
 * Date:2020/8/28
 * Time:15:32
 */
class ImageEngine : com.luck.picture.lib.engine.ImageEngine {
  //加载图片
  override fun loadImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgVerticalScreen(url)
  }

  //加载网络图片适配长图方案(此方法只有加载网络图片才会回调)
  override fun loadImage(
      context: Context,
      url: String,
      imageView: ImageView,
      longImageView: SubsamplingScaleImageView?,
      callback: OnImageCompleteCallback?
  ) {
    Utils.getApp().imageLoader.enqueue(
        ImageRequest.Builder(Utils.getApp()).data(url).target(
            onStart = {
              longImageView?.gone()
              imageView.visible()
              imageView.clear()
              imageView.setImageDrawable(PlaceHolderUtils.getLoadingHolder(720f / 1280))
            },
            onSuccess = { resource -> loadNetImage(resource.toBitmap(), imageView, longImageView) },
            onError = {
              longImageView?.gone()
              imageView.visible()
              imageView.clear()
              imageView.setImageDrawable(PlaceHolderUtils.getErrorHolder(720f / 1280))
            }
        ).build()
    )
  }

  //加载网络图片
  private fun loadNetImage(bitmap: Bitmap, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    val eqLongImage: Boolean = MediaUtils.isLongImg(bitmap.width, bitmap.height)
    longImageView?.visibility = if (eqLongImage) View.VISIBLE else View.GONE
    imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
    if (eqLongImage) {
      // 加载长图
      longImageView?.apply {
        isQuickScaleEnabled = true
        isZoomEnabled = true
        isPanEnabled = true
        setDoubleTapZoomDuration(100)
        setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
        setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
        setImage(ImageSource.bitmap(bitmap), ImageViewState(0f, PointF(0f, 0f), 0))
      }
    } else {
      // 普通图片
      imageView.setImageBitmap(bitmap)
    }
  }

  //已废弃
  override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    loadImage(context, url, imageView, longImageView, null)
  }

  //加载相册目录
  override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgSquare(url)
  }

  //加载gif
  override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgSquare(url)
  }

  //加载图片列表图片
  override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgSquare(url)
  }
}