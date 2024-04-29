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
    public boolean onTouchEvent(MotionEvent event) {
        //首先定义一个数组用来接收按钮的坐标xy值
        int[] xy = new int[2];

        //获取按钮的top/left xy值
        //button变量我在onCreat()函数中已经获取了控件，具体按实际情况写
        this.getLocationOnScreen(xy);

        //再定义一个数组用来计算控件的bottom/right xy值
        int[] xy_end = new int[2];
        xy_end[0] = this.getWidth() + xy[0];
        xy_end[1] = this.getHeight() + xy[1];
        //现在我们已经得到了按钮的左上坐标和右下坐标
        //两个点可以确定一个矩形嘛

        //event里包含了点击的信息；
        //我们判断点击的坐标是否在按钮坐标内，实际就是判断点击的xy值是否在上述矩形中；
        if (event.getX() >= xy[0] && event.getX() <= xy_end[0]
                && event.getY() >= xy[1] && event.getY() <= xy_end[1]) {
            //如果是，那么就执行里边的代码，在这里我们可以callOnClick()按钮

            //这里的代码说明
            //实际体验了一番，发现轻点一下和长按均可以激活按钮；
            //但是，我的按钮拥有animate()事件，所以连续点击会在动画未完成时再次点击按钮，
            //所以我做了个判断，让动画未完成时不再执行点击，机制如我
            //实际中，读者完全不用这两行代码
            //让我看看有哪些读者看都不看直接复制代码--手动滑稽
            //虽说站在巨人肩膀上，但是也要搞懂其原理才不会摔下来。
            if ( xy[0] >= this.getHeight())
                return false;
            Toast.makeText(activity, "点击了", Toast.LENGTH_SHORT).show();
            //我们callOnClick了按钮，也就是模拟点击了按钮；
            this.callOnClick();
            return false;
        }

        return super.onTouchEvent(event);
    }
    int[] xy_end = new int[2];

    private void init() {
        this.post(new Runnable() {
            @Override
            public void run() {
                getLocationOnScreen(location);
                rectF.left = location[0];
                rectF.top = location[1];
                rectF.right = location[0] + getWidth();
                rectF.bottom = location[1] + getHeight();





                Log.i("DEBUG", "rectF bounds: " + rectF.left + ", " + rectF.top + ", " + rectF.right + ", " + rectF.bottom);
            }
        });
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        rootView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float x = event.getRawX();
                        float y = event.getRawY();
                        // 更新ImageView的位置
                        getLocationOnScreen(location);
                        rectF.set(location[0], location[1], location[0] + getWidth(), location[1] + getHeight());
                        // 检查是否点击在ImageView内
                        if (rectF.contains(x, y)) {
                            Log.d("Touch", "T触摸在ImageView内部");

                            // 如果设置了点击事件监听器，则调用它
                            if (onRightClickListener != null) {
                                onRightClickListener.click();
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

    public  OnRightClickListener onRightClickListener;

    public void setOnClickListener( OnRightClickListener onClickListener) {
        this.onRightClickListener = onClickListener;
    }

    public interface OnRightClickListener {
        void click();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.clipRect(0, 0, with, height);
        super.onDraw(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        with = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }


}