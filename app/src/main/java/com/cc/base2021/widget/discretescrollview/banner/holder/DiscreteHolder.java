package com.cc.base2021.widget.discretescrollview.banner.holder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 参考： https://github.com/saiwu-bigkoo/Android-ConvenientBanner/blob/master/convenientbanner/src/main/java/com/bigkoo/convenientbanner/holder/Holder.java
 * Author:CASE
 * Date:2020/8/31
 * Time:10:12
 */
public abstract class DiscreteHolder<T> extends RecyclerView.ViewHolder {
  public DiscreteHolder(View itemView) {
    super(itemView);
  }

  public abstract void updateUI(T data, int position, int count);
}
