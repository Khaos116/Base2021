package com.cc.base2021.widget.discretescrollview.banner.listener;

/**
 * 参考：https://github.com/saiwu-bigkoo/Android-ConvenientBanner/blob/master/convenientbanner/src/main/java/com/bigkoo/convenientbanner/listener/OnItemClickListener.java
 * Author:Khaos
 * @date: 2019/10/14 12:07
 */
public interface OnItemClickListener<T> {
  void onItemClick(int position, T t);
}