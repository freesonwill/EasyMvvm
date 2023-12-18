package com.xcjh.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lzj on 2019/12/31
 * Describe ：字母排序索引
 */
public class SideBarSortView extends View {
    private Canvas mCanvas;
    private int mSelectIndex = 0;
    private float mTextSize;
    private int mTextColor;
    private float mTextSizeChoose;
    private int mTextColorChoose;
    //标记 避免重复调用
    private boolean isDown = false;
    private int lettersize=40;

    public void setmTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
    }

    public void setmTextSizeChoose(float mTextSizeChoose) {
        this.mTextSizeChoose = mTextSizeChoose;
    }

    public void setmTextColorChoose(int mTextColorChoose) {
        this.mTextColorChoose = mTextColorChoose;
    }
    public ArrayList<String> mList = new ArrayList<>();


    public Paint paint = new Paint();

    public SideBarSortView(Context context) {
        super(context);
    }

    public SideBarSortView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int desiredHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 计算每个字母项的高度
        int letterHeight = desiredHeight / lettersize;

        // 设置自定义视图的测量高度
        setMeasuredDimension(getMeasuredWidth(), letterHeight*mList.size()+40);
        int x=this.getHeight();

        // 设置自定义视图的高度为整体垂直居中
       // int paddingTop = (desiredHeight - letterHeight * mList.size()) / 2;
       // setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), getPaddingBottom());
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mCanvas = canvas;
        paintText();
    }

    public void setNewLetter(ArrayList<String> mLists ){
        mList.clear();
        mList.addAll(mLists);
        requestLayout(); // 重新布局
        invalidate();

    }
    private void paintText() {


        //计算每一个字母的高度,总告诉除以字母集合的高度就可以
        int height =1920/ lettersize;
        int y = getTop()+height/2;
        for (int i = 0; i < mList.size(); i++) {
            if (i == mSelectIndex) {
                paint.setColor(mTextColor);
                paint.setTextSize(mTextSizeChoose);
            } else {
                paint.setColor(mTextColor);
                paint.setTextSize(mTextSizeChoose);
            }
            paint.setAntiAlias(true);//设置抗锯齿
            //paint.setTypeface(Typeface.DEFAULT_BOLD);
            //计算每一个字母x轴
            float paintX = getWidth() / 2F - paint.measureText(mList.get(i)) / 2;
            //计算每一个字母Y轴
            int paintY = y+height*i;
            //int paintY = ((27-mList.size())/2*height)+i*height;
           // int paintY = height * i + height;
            //绘画出来这个TextView
            mCanvas.drawText(mList.get(i), paintX, paintY, paint);
            //画完一个以后重置画笔
            paint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        ViewParent parent;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int index = (int) (event.getY() / getHeight() * mList.size());
                if (index >= 0 && index < mList.size() && mSelectIndex != index) {
                    if (mClickListener != null) {
                        mClickListener.onSideBarScrollUpdateItem(mList.get(index));
                    }
                    mSelectIndex = index;
                    invalidate();
                    //改变标记状态
                    isDown = true;
                }
                parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);// 请求父级拦截，解决水平滑动冲突
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mClickListener != null) {
                    mClickListener.onSideBarScrollEndHideText();
                }
                //改变标记状态
                isDown = false;

                parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);// 请求父级放行，解决水平滑动冲突
                }
                break;
        }
        return true;
    }

    private OnIndexChangedListener mClickListener;

    public static interface OnIndexChangedListener {
        //滚动位置
        void onSideBarScrollUpdateItem(String word);

        //隐藏提示文本
        void onSideBarScrollEndHideText();
    }

    public void setIndexChangedListener(OnIndexChangedListener listener) {
        this.mClickListener = listener;
    }

    /**
     * Item滚动 更新侧边栏字母
     *
     * @param word 字母
     */
    public void onUpdateSideBarText(String word) {
        //手指没触摸才调用
        if (!isDown) {
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).equals(word) && mSelectIndex != i) {
                    mSelectIndex = i;
                    invalidate();
                }
            }
        }
    }

}

