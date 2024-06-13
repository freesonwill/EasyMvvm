package com.cn.game.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {
    private boolean isScrolling;
    private float downX;
    private float downY;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("BTBTBTBTBvv","2==========="+ev.getAction());
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                downX = ev.getX();
//                downY = ev.getY();
//                isScrolling = false;
//                Log.i("FFFFF","111111");
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i("FFFFF","222222");
//                float deltaX = Math.abs(ev.getX() - downX);
//                float deltaY = Math.abs(ev.getY() - downY);
//                if (deltaX > deltaY && deltaX > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
//                    isScrolling = true;
////                    return true;
//                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("FFFFF","33333333333333");
                break;
            case MotionEvent.ACTION_CANCEL:
//                isScrolling = false;
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
