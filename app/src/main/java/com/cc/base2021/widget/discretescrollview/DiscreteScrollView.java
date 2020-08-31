package com.cc.base2021.widget.discretescrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.*;
import androidx.recyclerview.widget.RecyclerView;
import com.cc.base2021.widget.discretescrollview.transform.DiscreteScrollItemTransformer;
import com.cc.base2021.widget.discretescrollview.util.ScrollListenerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 原lib：https://github.com/yarolegovich/DiscreteScrollView
 * 更新源文件，版本1.5.1，2020年8月31日10:49:52 by CASE
 * Created by yarolegovich on 18.02.2017.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DiscreteScrollView extends RecyclerView {

  public static final int NO_POSITION = DiscreteScrollLayoutManager.NO_POSITION;

  private static final int DEFAULT_ORIENTATION = DSVOrientation.HORIZONTAL.ordinal();

  private DiscreteScrollLayoutManager layoutManager;

  private List<ScrollStateChangeListener> scrollStateChangeListeners;
  private List<OnItemChangedListener> onItemChangedListeners;
  private Runnable notifyItemChangedRunnable = new Runnable() {
    @Override
    public void run() {
      notifyCurrentItemChanged();
    }
  };

  private boolean isOverScrollEnabled;
  //滑动停止后回调的位置
  private int endPosition = 0;
  //滑动过程中回调的位置
  private int scrollPosition = 0;

  public DiscreteScrollView(Context context) {
    super(context);
    init(null);
  }

  private void init(AttributeSet attrs) {
    scrollStateChangeListeners = new ArrayList<>();
    onItemChangedListeners = new ArrayList<>();

    isOverScrollEnabled = getOverScrollMode() != OVER_SCROLL_NEVER;

    layoutManager = new DiscreteScrollLayoutManager(
        getContext(), new ScrollStateListener(),
        DSVOrientation.values()[DEFAULT_ORIENTATION]);
    setLayoutManager(layoutManager);
  }

  @Override
  public void setLayoutManager(LayoutManager layout) {
    if (layout instanceof DiscreteScrollLayoutManager) {
      super.setLayoutManager(layout);
    } else {
      throw new IllegalArgumentException(
          "You should not set LayoutManager on DiscreteScrollView.class instance. Library uses a special one. Just don\\'t call the method.");
    }
  }

  @Override
  public boolean fling(int velocityX, int velocityY) {
    if (layoutManager.isFlingDisallowed(velocityX, velocityY)) {
      return false;
    }
    boolean isFling = super.fling(velocityX, velocityY);
    if (isFling) {
      layoutManager.onFling(velocityX, velocityY);
    } else {
      layoutManager.returnToCurrentPosition();
    }
    return isFling;
  }

  @Nullable
  public ViewHolder getViewHolder(int position) {
    View view = layoutManager.findViewByPosition(position);
    return view != null ? getChildViewHolder(view) : null;
  }

  @Override
  public void scrollToPosition(int position) {
    int currentPosition = layoutManager.getCurrentPosition();
    super.scrollToPosition(position);
    if (currentPosition != position) {
      notifyCurrentItemChanged();
    }
  }

  /**
   * @return adapter position of the current item or -1 if nothing is selected
   */
  public int getCurrentItem() {
    return layoutManager.getCurrentPosition();
  }

  public void setItemTransformer(DiscreteScrollItemTransformer transformer) {
    layoutManager.setItemTransformer(transformer);
  }

  public void setItemTransitionTimeMillis(@IntRange(from = 10) int millis) {
    layoutManager.setTimeForItemSettle(millis);
  }

  public void setSlideOnFling(boolean result) {
    layoutManager.setShouldSlideOnFling(result);
  }

  public void setSlideOnFlingThreshold(int threshold) {
    layoutManager.setSlideOnFlingThreshold(threshold);
  }

  public void setOrientation(DSVOrientation orientation) {
    layoutManager.setOrientation(orientation);
  }

  public void setOffscreenItems(int items) {
    layoutManager.setOffscreenItems(items);
  }

  public void setScrollConfig(@NonNull DSVScrollConfig config) {
    layoutManager.setScrollConfig(config);
  }

  public void setClampTransformProgressAfter(@IntRange(from = 1) int itemCount) {
    if (itemCount <= 1) {
      throw new IllegalArgumentException("must be >= 1");
    }
    layoutManager.setTransformClampItemCount(itemCount);
  }

  public void setOverScrollEnabled(boolean overScrollEnabled) {
    isOverScrollEnabled = overScrollEnabled;
    setOverScrollMode(OVER_SCROLL_NEVER);
  }

  public void addScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
    scrollStateChangeListeners.add(scrollStateChangeListener);
  }

  public void addScrollListener(@NonNull ScrollListener<?> scrollListener) {
    addScrollStateChangeListener(new ScrollListenerAdapter(scrollListener));
  }

  public void addOnItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
    onItemChangedListeners.add(onItemChangedListener);
  }

  public void removeScrollStateChangeListener(@NonNull ScrollStateChangeListener<?> scrollStateChangeListener) {
    scrollStateChangeListeners.remove(scrollStateChangeListener);
  }

  public void removeScrollListener(@NonNull ScrollListener<?> scrollListener) {
    removeScrollStateChangeListener(new ScrollListenerAdapter<>(scrollListener));
  }

  public void removeItemChangedListener(@NonNull OnItemChangedListener<?> onItemChangedListener) {
    onItemChangedListeners.remove(onItemChangedListener);
  }

  private void notifyScrollStart(ViewHolder holder, int current) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScrollStart(holder, current);
    }
  }

  private void notifyScrollEnd(ViewHolder holder, int current) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScrollEnd(holder, current);
    }
  }

  private void notifyScroll(float position,
      int currentIndex, int newIndex,
      ViewHolder currentHolder, ViewHolder newHolder) {
    for (ScrollStateChangeListener listener : scrollStateChangeListeners) {
      listener.onScroll(position, currentIndex, newIndex,
          currentHolder,
          newHolder);
    }
  }

  private void notifyCurrentItemChanged(ViewHolder holder, int current, boolean end) {
    if (end && endPosition == current) return;//如果滑动结束后回调位置和上次回调的一样，则不再进行回调
    if (end) endPosition = current;//记录滑动结束的位置
    if (!end && scrollPosition == current) return;//如果滑动过程中回调位置的位置和上次回调位置一样，则不再进行回调
    if (!end) scrollPosition = current;//记录滑动过程中的位置
    for (OnItemChangedListener listener : onItemChangedListeners) {
      listener.onCurrentItemChanged(holder, current, end);
    }
  }

  private void notifyCurrentItemChanged() {
    removeCallbacks(notifyItemChangedRunnable);
    if (onItemChangedListeners.isEmpty()) {
      return;
    }
    int current = layoutManager.getCurrentPosition();
    ViewHolder currentHolder = getViewHolder(current);
    if (currentHolder == null) {
      post(notifyItemChangedRunnable);
    } else {
      notifyCurrentItemChanged(currentHolder, current, false);
    }
  }

  private class ScrollStateListener implements DiscreteScrollLayoutManager.ScrollStateListener {

    //是否执行预览
    private boolean preview = false;

    @Override
    public void onIsBoundReachedFlagChange(boolean isBoundReached) {
      if (isOverScrollEnabled) {
        setOverScrollMode(isBoundReached ? OVER_SCROLL_ALWAYS : OVER_SCROLL_NEVER);
      }
    }

    @Override
    public void onScrollStart() {
      removeCallbacks(notifyItemChangedRunnable);
      if (scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int current = layoutManager.getCurrentPosition();
      ViewHolder holder = getViewHolder(current);
      if (holder != null) {
        notifyScrollStart(holder, current);
      }
    }

    @Override
    public void onScrollEnd() {
      if (onItemChangedListeners.isEmpty() && scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int current = layoutManager.getCurrentPosition();
      ViewHolder holder = getViewHolder(current);
      if (holder != null) {
        notifyScrollEnd(holder, current);
        notifyCurrentItemChanged(holder, current, true);
      }
    }

    @Override
    public void onScroll(float currentViewPosition) {
      //==新增，手指不放，滑动到完全显示执行回调(★★这只是预览回调，手指放开才是真的回调★★)==//
      if (currentViewPosition > 0.5f && !preview) {//上一页
        preview = true;
        int position = getCurrentItem() - 1;
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position, false);
      } else if (currentViewPosition < -0.5f && !preview) {//下一页
        preview = true;
        int position = getCurrentItem() + 1;
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position, false);
      } else if (currentViewPosition > -0.5f && currentViewPosition < 0.5f && preview) {//当前页
        preview = false;
        int position = getCurrentItem();
        ViewHolder holder = getViewHolder(position);
        notifyCurrentItemChanged(holder, position, false);
      }
      //===============================================================================//

      if (scrollStateChangeListeners.isEmpty()) {
        return;
      }
      int currentIndex = getCurrentItem();
      int newIndex = layoutManager.getNextPosition();
      if (currentIndex != newIndex) {
        notifyScroll(currentViewPosition,
            currentIndex, newIndex,
            getViewHolder(currentIndex),
            getViewHolder(newIndex));
      }
    }

    @Override
    public void onCurrentViewFirstLayout() {
      notifyCurrentItemChanged();
    }

    @Override
    public void onDataSetChangeChangedPosition() {
      notifyCurrentItemChanged();
    }
  }

  public interface ScrollStateChangeListener<T extends ViewHolder> {

    void onScrollStart(@NonNull T currentItemHolder, int adapterPosition);

    void onScrollEnd(@NonNull T currentItemHolder, int adapterPosition);

    void onScroll(float scrollPosition,
        int currentPosition,
        int newPosition,
        @Nullable T currentHolder,
        @Nullable T newCurrent);
  }

  public interface ScrollListener<T extends ViewHolder> {

    void onScroll(float scrollPosition,
        int currentPosition, int newPosition,
        @Nullable T currentHolder,
        @Nullable T newCurrent);
  }

  public interface OnItemChangedListener<T extends ViewHolder> {
    /*
     * This method will be also triggered when view appears on the screen for the first time.
     * If data set is empty, viewHolder will be null and adapterPosition will be NO_POSITION
     */
    void onCurrentItemChanged(@Nullable T viewHolder, int adapterPosition, boolean end);
  }
}