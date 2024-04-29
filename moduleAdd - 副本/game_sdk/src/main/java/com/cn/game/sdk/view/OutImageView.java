package com.cn.game.sdk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 *
 *
 */
public class OutImageView extends AppCompatImageView {
    private int[] location = new int[2];
    private RectF rectF = new RectF();
    private Activity activity;
    private int with, height;

    public OutImageView(@NonNull Context context) {
        this(context, null);
    }

    public OutImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        init();
    }



    private void init() {
        this.post(new Runnable() {
            @Override
            public void run() {
                getLocationOnScreen(location);
                rectF.left = location[0];
                rectF.top = location[1];
                rectF.right = location[0] + getWidth();
                rectF.bottom = location[1] + getHeight();

                with = getWidth();
                height = getHeight();

            }
        });
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        rootView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        float x = event.getX();
//                        float y = event.getY();
//                        // 更新ImageView的位置
//                        getLocationOnScreen(location);
//
//                        rectF.set(location[0], location[1], location[0] + getWidth(), location[1] + getHeight());
                        float x = event.getX();
                        float y = event.getY();
                        getLocationOnScreen(location);
                        rectF.set(location[0], location[1], location[0] + getWidth(), location[1] + getHeight());
                        Log.i("多大发的","宽度="+with+"高度="+ height);
                        // 检查是否点击在ImageView内
                        Log.i("SSSSSSSSSs","centerX="+rectF.centerX()+"centerY="+rectF.centerY());
                        Log.i("SSSSSSSSSs","x="+x+"y="+y);
                        if (rectF.contains(x, y) && getVisibility() == VISIBLE) {
                            Log.d("Touch", "T触摸在ImageView内部");

                            // 如果设置了点击事件监听器，则调用它
                            if (onClickListener != null) {
                                onClickListener.click();
                            }
                            return true; // 表示触摸事件被处理
                        }else {
                            Log.d("Touch", "=====没有摸到");
                            return false; // 不处理触摸事件
                        }


//                        if (rectF.contains(event.getRawX(), event.getRawY())&&getVisibility()==VISIBLE) {
//                            Toast.makeText(activity, "点击了", Toast.LENGTH_SHORT).show();
//                            if (onClickListener != null)
//                                onClickListener.click();
//                        }
//                        break;
                }
                return false;
            }
        });
    }

    public OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void click();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.clipRect(0, 0, with, height);
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        with = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }


}