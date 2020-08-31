package com.cc.base2021.widget.sticky;

import android.view.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Author: 温利东 on 2017/6/17 14:24.
 * http://www.jianshu.com/u/99f514ea81b3
 * github: https://github.com/LidongWen
 */
public class StickyAnyHeader {
  private StickyAnyAdapter adapter;
  private RecyclerView recyclerView;
  private StickyAnyDecoration decor;
  private int gravity = Gravity.LEFT;

  public StickyAnyHeader() {
  }

  public StickyAnyHeader adapter(StickyAnyAdapter adapter) {
    this.adapter = adapter;
    return this;
  }

  public StickyAnyHeader setRecyclerView(RecyclerView recyclerView) {
    this.recyclerView = recyclerView;
    return this;
  }

  /**
   * @param gravity Gravity.Left  Gravity.Right   Gravity.center
   */
  private StickyAnyHeader gravity(int gravity) {
    this.gravity = gravity;
    return this;
  }

  public void togo() {
    goSticky(adapter, recyclerView);
  }

  private void goSticky(StickyAnyAdapter adapter, RecyclerView recyclerView) {
    if (adapter == null || recyclerView == null) {
      throw new NullPointerException("parameter is Null !  class "
          + this.getClass().getName() + " methon" + Thread.currentThread().getStackTrace()[1].getMethodName());
    }
    decor = new StickyAnyDecoration(adapter);
    ArrayList<RecyclerView.ItemDecoration> property = getItemDecorationsAndClearOld(recyclerView);
    findMyListernerAndClear(recyclerView);

    recyclerView.addItemDecoration(decor, property.size());
    recyclerView.addOnItemTouchListener(new MyOnItemToucherListerner() {
      boolean b = false;

      @Override
      public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            View view = decor.findHeaderView((int) event.getX(), (int) event.getY());
            if (view != null) {
              b = true;
              return true;
            }
          case MotionEvent.ACTION_UP:
            View view2 = decor.findHeaderView((int) event.getX(), (int) event.getY());
            if (b && view2 != null) {
              b = false;
              view2.performClick();
              return true;
            }
            b = false;
        }
        return false;
      }

      @Override
      public void onTouchEvent(RecyclerView rv, MotionEvent event) {
        //                switch (event.getAction()) {
        //                    case MotionEvent.ACTION_UP:
        //                        View view2 = decor.findHeaderView((int) event.getX(), (int) event.getY());
        //                        if (view2 != null) {
        //                            view2.performClick();
        //                        }
        //                }
      }

      @Override
      public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

      }
    });
  }

  @NonNull
  public ArrayList<RecyclerView.ItemDecoration> getItemDecorationsAndClearOld(RecyclerView recyclerView) {
    ArrayList<RecyclerView.ItemDecoration> property = null;
    try {
      property = (ArrayList) getField(recyclerView, "mItemDecorations");
      for (Object o : property) {
        if (o instanceof StickyAnyDecoration) {
          recyclerView.removeItemDecoration((StickyAnyDecoration) o);
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return property;
  }

  private void findMyListernerAndClear(RecyclerView recyclerView) {

    ArrayList<?> property = null;
    try {
      property = (ArrayList) getField(recyclerView, "mOnItemTouchListeners");
      for (Object o : property) {
        if (o instanceof MyOnItemToucherListerner) {
          recyclerView.removeOnItemTouchListener((MyOnItemToucherListerner) o);
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void notifyDataSetChanged(RecyclerView recyclerView) {
    ArrayList<?> property = null;
    try {
      property = (ArrayList) getField(recyclerView, "mItemDecorations");
      for (Object o : property) {
        if (o instanceof StickyAnyDecoration) {
          ((StickyAnyDecoration) o).clearHeaderCache();
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Object getField(Object owner, String fieldName) throws Exception {

    Class<?> ownerClass = owner.getClass();
    Field field = null;
    Object obj = new Object();
    try {
      Field[] fields = ownerClass.getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        field = fields[i];
        if (field.getName().equals(fieldName)) {
          field.setAccessible(true);
          break;
        }
      }
      obj = field.get(owner);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return obj;
  }

  private interface MyOnItemToucherListerner extends RecyclerView.OnItemTouchListener {

  }
}