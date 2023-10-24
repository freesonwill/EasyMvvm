package com.xcjh.app.ui.details.common;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

import javax.annotation.Nullable;

/**
 * 可以控制NestedScrollingChild2接口的Recyclerview
 *
 * @author Administrator
 */
public class NoNestedRecyclerView extends RecyclerView {
    boolean isNestedEnable = false;

    public NoNestedRecyclerView(Context context) {
        super(context);
    }

    public NoNestedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoNestedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isNestedEnable() {
        return isNestedEnable;
    }

    public void setNestedEnable(boolean nestedEnable) {
        isNestedEnable = nestedEnable;
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        if (isNestedEnable) {
            return super.startNestedScroll(axes, type);
        } else {
            return false;
        }
    }

    @Override
    public void stopNestedScroll(int type) {
        if (isNestedEnable) {
            super.stopNestedScroll(type);
        }
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        if (isNestedEnable) {
            return super.hasNestedScrollingParent(type);
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type) {
        if (isNestedEnable) {
            return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (isNestedEnable) {
            return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        } else {
            return false;
        }
    }
}

