package com.cn.game.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class CustomViewGroup  extends RelativeLayout {

    public CustomViewGroup(Context context) {
        super(context);
    }

    public CustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.i("SSSSSSSSSSSSSAA","============");

        // 在此处添加逻辑以判断是否拦截事件
        // 如果需要将事件传递给子视图，则返回 false
        // 如果需要拦截事件，则返回 true
        return false;
    }


}