package com.cc.base2021.widget.discretescrollview;
public enum DSVScrollConfig {
  ENABLED {
    @Override
    boolean isScrollBlocked(Direction direction) {
      return false;
    }
  },
  FORWARD_ONLY {
    @Override
    boolean isScrollBlocked(Direction direction) {
      return direction == Direction.START;
    }
  },
  BACKWARD_ONLY {
    @Override
    boolean isScrollBlocked(Direction direction) {
      return direction == Direction.END;
    }
  },
  DISABLED {
    @Override
    boolean isScrollBlocked(Direction direction) {
      return true;
    }
  };

  abstract boolean isScrollBlocked(Direction direction);
}