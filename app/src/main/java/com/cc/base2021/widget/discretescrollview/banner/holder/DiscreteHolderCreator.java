package com.cc.base2021.widget.discretescrollview.banner.holder;

import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * 参考：https://github.com/saiwu-bigkoo/Android-ConvenientBanner/blob/master/convenientbanner/src/main/java/com/bigkoo/convenientbanner/holder/CBViewHolderCreator.java
 *
 * @author: CASE
 * @date: 2019/10/14 11:37
 */
public interface DiscreteHolderCreator {
  int getLayoutId();

  DiscreteHolder createHolder(@NotNull View itemView);
}
