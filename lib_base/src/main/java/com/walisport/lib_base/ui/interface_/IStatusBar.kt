package com.walisport.lib_base.ui.interface_

/**
 * @author: zhangsan
 * @date: 2025/3/31 14:24
 * @description:
 */
interface IStatusBar {
    /**
     * 设置状态栏颜色
     * @param color
     */
    fun setStatusBarColor(color:Int):IStatusBar

    /**
     * 设置状态栏显隐
     * @param b
     */
    fun setStatusBarVisible(b:Boolean):IStatusBar

    /**
     * 生效状态栏
     */
    fun applyStatusBar()
}