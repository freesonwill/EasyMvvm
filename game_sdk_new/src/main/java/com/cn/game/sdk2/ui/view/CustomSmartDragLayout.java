package com.cn.game.sdk2.ui.view;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.lxj.xpopup.enums.LayoutStatus;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.SmartDragLayout;
import com.xcjh.base_lib2.utils.LogUtils;

public class CustomSmartDragLayout extends LinearLayout implements NestedScrollingParent {
    private View child;
    OverScroller scroller;
    VelocityTracker tracker;
    boolean enableDrag;
    boolean dismissOnTouchOutside;
    boolean isUserClose;
    boolean isThreeDrag;
    LayoutStatus status;
    int duration;
    int maxY;
    int minY;
    int lastHeight;
    float touchX;
    float touchY;
    boolean isScrollUp;
    private SmartDragLayout.OnCloseListener listener;

    public CustomSmartDragLayout(Context context) {
        this(context, (AttributeSet)null);
    }

    public CustomSmartDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSmartDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.enableDrag = true;
        this.dismissOnTouchOutside = true;
        this.isUserClose = false;
        this.isThreeDrag = false;
        this.status = LayoutStatus.Close;
        this.duration = 400;
        if (this.enableDrag) {
            this.scroller = new OverScroller(context);
        }

    }

    public void onViewAdded(View c) {
        super.onViewAdded(c);
        this.child = c;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l;
        if (this.enableDrag) {
            if (this.child == null) {
                return;
            }

            this.maxY = this.child.getMeasuredHeight();
            this.minY = 0;
            l = this.getMeasuredWidth() / 2 - this.child.getMeasuredWidth() / 2;
            this.child.layout(l, this.getMeasuredHeight(), l + this.child.getMeasuredWidth(), this.getMeasuredHeight() + this.maxY);
            if (this.status == LayoutStatus.Open) {
                if (this.isThreeDrag) {
                    this.scrollTo(this.getScrollX(), this.getScrollY() - (this.lastHeight - this.maxY));
                } else {
                    this.scrollTo(this.getScrollX(), this.getScrollY() - (this.lastHeight - this.maxY));
                }
            }

            this.lastHeight = this.maxY;
        } else {
            l = this.getMeasuredWidth() / 2 - this.child.getMeasuredWidth() / 2;
            this.child.layout(l, this.getMeasuredHeight() - this.child.getMeasuredHeight(), l + this.child.getMeasuredWidth(), this.getMeasuredHeight());
        }

    }
    private float _touchX = 0f;
    private float _touchY = 0f;
    private boolean isTrigger = false;
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.isUserClose = true;
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                _touchX = ev.getX();
                _touchY = ev.getY();
                isTrigger = false;
                onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                float x  = ev.getX() - _touchX;
                float y = ev.getY() - _touchY;
                if (isTrigger || (Math.abs(x) > Math.abs(y) && Math.abs(x) > 5)) {
                    isTrigger = true;
                    _touchX = ev.getX();
                    _touchY = ev.getY();
                    return true;
                }
                break;
        }
        return this.status != LayoutStatus.Closing && this.status != LayoutStatus.Opening && super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.status != LayoutStatus.Closing && this.status != LayoutStatus.Opening) {
            if (this.enableDrag && (this.scroller.computeScrollOffset() || this.status == LayoutStatus.Close)) {
                this.touchX = 0.0F;
                this.touchY = 0.0F;
                return true;
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (this.enableDrag) {
                            if (this.tracker != null) {
                                this.tracker.clear();
                            }

                            this.tracker = VelocityTracker.obtain();
                        }

                        this.touchX = event.getX();
                        this.touchY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        Rect rect = new Rect();
                        this.child.getGlobalVisibleRect(rect);
                        float yVelocity;
                        if (!XPopupUtils.isInRect(event.getRawX(), event.getRawY(), rect) && this.dismissOnTouchOutside) {
                            yVelocity = (float)Math.sqrt(Math.pow((double)(event.getX() - this.touchX), 2.0) + Math.pow((double)(event.getY() - this.touchY), 2.0));
                            if (yVelocity < (float)ViewConfiguration.get(this.getContext()).getScaledTouchSlop()) {
                                this.performClick();
                            }
                        }

                        if (this.enableDrag && this.tracker != null) {
                            yVelocity = this.tracker.getYVelocity();
                            if (yVelocity > 1500.0F && !this.isThreeDrag) {
                                this.close();
                            } else {
                                this.finishScroll();
                            }

                            this.tracker = null;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (this.enableDrag && this.tracker != null) {
                            this.tracker.addMovement(event);
                            this.tracker.computeCurrentVelocity(1000);
                            int dy = (int)(event.getY() - this.touchY);
                            if(Math.abs(event.getX() - this.touchX) > Math.abs(event.getY() - this.touchY)) {
                                dy = (int)((event.getX() - this.touchX) * 1f);
                            }
                            this.scrollTo(this.getScrollX(), this.getScrollY() - dy);
                            this.touchX = event.getX();
                            this.touchY = event.getY();
                        }
                }

                return this.enableDrag;
            }
        } else {
            return false;
        }
    }

    private void finishScroll() {
        if (this.enableDrag) {
            int threshold = this.isScrollUp ? (this.maxY - this.minY) / 3 : (this.maxY - this.minY) * 2 / 3;
            int dy = (this.getScrollY() > threshold ? this.maxY : this.minY) - this.getScrollY();
            if (this.isThreeDrag) {
                int per = this.maxY / 3;
                if ((float)this.getScrollY() > (float)per * 2.5F) {
                    dy = this.maxY - this.getScrollY();
                } else if ((float)this.getScrollY() <= (float)per * 2.5F && (float)this.getScrollY() > (float)per * 1.5F) {
                    dy = per * 2 - this.getScrollY();
                } else if (this.getScrollY() > per) {
                    dy = per - this.getScrollY();
                } else {
                    dy = this.minY - this.getScrollY();
                }
            }
            if(dy != 0) {
                this.scroller.startScroll(this.getScrollX(), this.getScrollY(), 0, dy, this.duration);
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }

    }

    public void scrollTo(int x, int y) {
        if (y > this.maxY) {
            y = this.maxY;
        }

        if (y < this.minY) {
            y = this.minY;
        }

        float fraction = (float)(y - this.minY) * 1.0F / (float)(this.maxY - this.minY);
        this.isScrollUp = y > this.getScrollY();
        if (this.listener != null) {
            if (this.isUserClose && fraction == 0.0F && this.status != LayoutStatus.Close) {
                this.status = LayoutStatus.Close;
                this.listener.onClose();
            } else if (fraction == 1.0F && this.status != LayoutStatus.Open) {
                this.status = LayoutStatus.Open;
                this.listener.onOpen();
            }

            this.listener.onDrag(y, fraction, this.isScrollUp);
        }

        super.scrollTo(x, y);
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.scroller.computeScrollOffset()) {
            this.scrollTo(this.scroller.getCurrX(), this.scroller.getCurrY());
            ViewCompat.postInvalidateOnAnimation(this);
        }

    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isScrollUp = false;
        this.isUserClose = false;
        this.setTranslationY(0.0F);
    }

    public void open() {
        this.post(new Runnable() {
            public void run() {
                int dy = CustomSmartDragLayout.this.maxY - CustomSmartDragLayout.this.getScrollY();
                CustomSmartDragLayout.this.smoothScroll(CustomSmartDragLayout.this.enableDrag && CustomSmartDragLayout.this.isThreeDrag ? dy / 3 : dy, true);
                CustomSmartDragLayout.this.status = LayoutStatus.Opening;
            }
        });
    }

    public void close() {
        this.isUserClose = true;
        this.post(new Runnable() {
            public void run() {
                CustomSmartDragLayout.this.scroller.abortAnimation();
                CustomSmartDragLayout.this.smoothScroll(CustomSmartDragLayout.this.minY - CustomSmartDragLayout.this.getScrollY(), false);
                CustomSmartDragLayout.this.status = LayoutStatus.Closing;
            }
        });
    }

    private void smoothScroll(final int dy, final boolean isOpen) {
        this.scroller.startScroll(this.getScrollX(), this.getScrollY(), 0, dy, (int)(isOpen ? (float)this.duration : (float)this.duration * 0.8F));
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        RecyclerView rv = null;
        if(this.child instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) this.child).getChildCount(); i++) {
                View v = ((ViewGroup) this.child).getChildAt(i);
                if(v instanceof RecyclerView){
                    rv = (RecyclerView) v;
                    break;
                }
            }
        }
        if(rv != null) {
            //滚到底部不截获，否则没有overScroll效果
            if(!rv.canScrollVertically(1)) return false;
        }
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && this.enableDrag;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        this.scroller.abortAnimation();
    }

    public void onStopNestedScroll(View target) {
        this.finishScroll();
    }

    //子的滚动布局
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        //LogUtils.d("dxConsumed:"+dxConsumed+",dyConsumed:"+dyConsumed+",dxUnconsumed:"+dxUnconsumed+",dyUnconsumed:"+dyUnconsumed);
        if(dyUnconsumed != 0)
            this.scrollTo(this.getScrollX(), this.getScrollY() + dyUnconsumed);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (dy > 0) {
            int newY = this.getScrollY() + dy;
            if (newY < this.maxY) {
                consumed[1] = dy;
            }

            this.scrollTo(this.getScrollX(), newY);
        }

    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        boolean isDragging = this.getScrollY() > this.minY && this.getScrollY() < this.maxY;
        if (isDragging && velocityY < -1500.0F && !this.isThreeDrag) {
            this.close();
        }
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    public int getNestedScrollAxes() {
        return ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    public void isThreeDrag(boolean isThreeDrag) {
        this.isThreeDrag = isThreeDrag;
    }

    public void enableDrag(boolean enableDrag) {
        this.enableDrag = enableDrag;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void dismissOnTouchOutside(boolean dismissOnTouchOutside) {
        this.dismissOnTouchOutside = dismissOnTouchOutside;
    }

    public void setOnCloseListener(SmartDragLayout.OnCloseListener listener) {
        this.listener = listener;
    }

}
