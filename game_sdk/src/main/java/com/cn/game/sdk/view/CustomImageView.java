package com.cn.game.sdk.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomImageView extends AppCompatImageView {

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            // 获取当前ImageView的可见矩形范围
            Rect rect = new Rect();
            getLocalVisibleRect(rect);

            // 检查触摸事件的坐标是否在可见矩形范围内
            if (rect.contains(x, y)) {
                Log.i("BBBBBBBBB","点击成功");
                return true;
            }
        }

        // 返回false表示事件未被处理
        return super.onTouchEvent(event);
    }
}
