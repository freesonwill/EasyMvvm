package com.xcjh.base_lib.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * 描述　:使用DataBinding时使用该库 https://github.com/whataa/noDrawable
 * 只需要复制核心类 ProxyDrawable，Drawables至项目中即可
 * 可以减少大量的drawable.xml文件
 */
public class ProxyDrawable extends StateListDrawable {

    private Drawable originDrawable;

    @Override
    public void addState(int[] stateSet, Drawable drawable) {
        if (stateSet != null && stateSet.length == 1 && stateSet[0] == 0) {
            originDrawable = drawable;
        }
        super.addState(stateSet, drawable);
    }

    Drawable getOriginDrawable() {
        return originDrawable;
    }
}
