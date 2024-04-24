package com.cn.game.sdk.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class RoundedCornerView extends RelativeLayout {
    private final RectF mBounds;

    public RoundedCornerView(Context context) {
        super(context);
        // 初始化圆角矩形的边界
        mBounds = new RectF(40,40,0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 在这里绘制你的带有圆角的背景
        // 例如使用 Paint 和 Path 来绘制圆角矩形
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 检查触摸点是否在圆角矩形内
        if (mBounds.contains(event.getX(), event.getY())) {
            // 在这里处理点击事件
            return true;
        }
        return false;
    }
}
