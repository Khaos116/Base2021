package com.cc.base2021.widget.discretescrollview.holder;

import android.view.View;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 *
 * @author: caiyoufei
 * @date: 2019/10/14 11:37
 */
public interface DiscreteHolderCreator {
  DiscreteHolder createHolder(@NotNull View itemView);

  int getLayoutId();
}
