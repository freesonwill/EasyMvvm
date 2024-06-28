package com.cn.game.sdk2.utils.ext

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.fragment.BaseVmFragment

/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/20 10:59
 **/
object ViewExt {

    inline val View.locationOnScreen:IntArray get(){
        val pos = IntArray(2)
        this.getLocationOnScreen(pos)
        return pos
    }

    inline val View.locationInWindow:IntArray get(){
        val pos = IntArray(2)
        this.getLocationInWindow(pos)
        return pos
    }

    inline val View.locationInSurface:IntArray  @RequiresApi(Build.VERSION_CODES.Q) get(){
        val pos = IntArray(2)
        this.getLocationInSurface(pos)
        return pos
    }

    //是否在View区域内
    fun View.isInArea(rawX:Float,rawY:Float):Boolean{
        val rawXY = IntArray(2)
        getLocationOnScreen(rawXY)
        return rawX >= rawXY[0] && rawX <= (rawXY[0] + width) && rawY >= rawXY[1] && rawY <= (rawXY[1] + height)
    }

    fun BaseVmFragment<*>.getDrawable(@DrawableRes id:Int):Drawable{
        return  ContextCompat.getDrawable(requireContext(),id)!!
    }

    @ColorInt fun BaseVmFragment<*>.getColor(@ColorRes id:Int): Int {
        return  ContextCompat.getColor(requireContext(),id)
    }

}