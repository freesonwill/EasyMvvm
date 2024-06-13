package com.cn.game.sdk.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class OutImageViewRight extends AppCompatImageView {
    private int[] location = new int[2];
    private RectF rectF = new RectF();
    private Activity activity;
    private int with, height;

    public OutImageViewRight(@NonNull Context context) {
        this(context, null);
    }

    public OutImageViewRight(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutImageViewRight(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        activity = (Activity) context;
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.i("UUUUUUUUUUUUUUUUUUUuu","11111111111111111");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("UUUUUUUUUUUUUUUUUUUuu","2222222222222222");
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        Log.i("UUUUUUUUUUUUUUUUUUUuu","33333333333333");
    }

    @Override
    public void invalidate() {
        super.invalidate();
        Log.i("UUUUUUUUUUUUUUUUUUUuu","444444444");

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
    }

    private void init() {
//        val viewTreeObserver = showRightTopMoney.viewTreeObserver
//        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                // 确保只监听一次
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    showRightTopMoney.viewTreeObserver.removeGlobalOnLayoutListener(this)
//                } else {
//                    showRightTopMoney.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//
//
//            }
//        })




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

                        Log.i("BBBBBB","x========="+event.getRawX()+"======y==="+event.getRawY());

                        if (rectF.contains(event.getRawX(), event.getRawY())&&getVisibility()==VISIBLE) {
                            Log.d("Touch", "11T触摸在ImageView内部");

                            // 如果设置了点击事件监听器，则调用它
                            if (onClickListener != null) {
                                onClickListener.click();
                            }
                            return true; // 表示触摸事件被处理
                        }else {
                            Log.d("Touch", "111=====没有摸到");
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

    public  OnClickListener onClickListener;

    public void setOnClickListener( OnClickListener onClickListener) {
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