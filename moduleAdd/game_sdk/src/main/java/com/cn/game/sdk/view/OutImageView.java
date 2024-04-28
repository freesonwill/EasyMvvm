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
            }
        });
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        rootView.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (rectF.contains(event.getRawX(), event.getRawY())&&getVisibility()==VISIBLE) {
//                            MyToast.addToast("我被点击了");
                            if (onClickListener != null)
                                onClickListener.click();
                        }
                        break;
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
