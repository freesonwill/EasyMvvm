/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xcjh.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.github.gzuliyujiang.wheelview.widget.WheelView;
import com.kongzue.dialogx.interfaces.ScrollController;


/**
 * 嵌套ScrollView
 *
 * @author venshine
 */
public class NestedScrollView extends ScrollView implements ScrollController {

    public NestedScrollView(Context context) {
        super(context);
        init();
    }

    public NestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener((v, event) -> {
            WheelView wv = findViewWithTag(WheelConstants.TAG);
            if (wv != null) {
                wv.getParent().requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });
    }
    @Override
//请按照固定写法
    public boolean isLockScroll() {
        return lockScroll;
    }

    boolean lockScroll;

    @Override
//请按照固定写法
    public void lockScroll(boolean lockScroll) {
        this.lockScroll=lockScroll;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (lockScroll) return false;
        return super.onTouchEvent(event);
    }

    @Override
//获取已滚动距离，请注意不同组件的获取方式不同，请按照根据实际情况调整
    public int getScrollDistance() {
        return getScrollY();
    }

    @Override
//固定写法
    public boolean isCanScroll() {
        return true;
    }

}
