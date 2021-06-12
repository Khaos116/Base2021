package com.cc.base2021.widget.picsel

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.*
import coil.request.ImageRequest
import com.blankj.utilcode.util.Utils
import com.cc.base2021.comm.CommActivity
import com.cc.base2021.ext.loadImgSquare
import com.cc.base2021.ext.loadImgVertical
import com.cc.base2021.utils.PlaceHolderUtils
import com.cc.ext.gone
import com.cc.ext.visible
import com.luck.picture.lib.PictureSelectorActivity
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.*
import java.lang.ref.WeakReference

/**
 * Author:Khaos
 * Date:2020/8/28
 * Time:15:32
 */
class ImageEngine : com.luck.picture.lib.engine.ImageEngine {
  //加载图片
  override fun loadImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgVertical(url)
  }

  //加载网络图片适配长图方案(此方法只有加载网络图片才会回调)
  override fun loadImage(
      context: Context,
      url: String,
      imageView: ImageView,
      longImageView: SubsamplingScaleImageView?,
      callback: OnImageCompleteCallback?
  ) {
    val weakReference = WeakReference(imageView)
    val weakReferenceLong = WeakReference(longImageView)
    Utils.getApp().imageLoader.enqueue(
        ImageRequest.Builder(Utils.getApp()).data(url).target(
            onStart = {
              weakReferenceLong.get()?.gone()
              weakReference.get()?.let { iv ->
                iv.visible()
                iv.clear()
                iv.setImageDrawable(PlaceHolderUtils.getLoadingHolder(720f / 1280))
              }
            },
            onSuccess = { resource -> weakReference.get()?.let { iv -> loadNetImage(resource, iv, weakReferenceLong.get()) } },
            onError = {
              weakReferenceLong.get()?.gone()
              weakReference.get()?.let { iv ->
                iv.visible()
                iv.clear()
                iv.setImageDrawable(PlaceHolderUtils.getErrorHolder(720f / 1280))
              }
            }
        ).build()
    )
  }

  //加载网络图片
  private fun loadNetImage(drawable: Drawable, imageView: ImageView, longImageView: SubsamplingScaleImageView?) {
    val bitmap = drawable.toBitmap()
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
      imageView.load(drawable)
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
    if (context is CommActivity || context is PictureSelectorActivity) {
      imageView.loadImgSquare(url)
    } else {
      imageView.loadImgVertical(url)
    }
  }

  //加载图片列表图片
  override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
    imageView.loadImgSquare(url)
  }
}