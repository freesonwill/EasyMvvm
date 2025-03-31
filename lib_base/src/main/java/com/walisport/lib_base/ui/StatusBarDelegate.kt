package com.walisport.lib_base.ui

import android.app.Activity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.walisport.lib_base.ui.interface_.IStatusBar

/**
 * @author: zhangsan
 * @date: 2025/3/31 14:26
 * @description:
 */
class StatusBarDelegate(private val activity: Activity) : IStatusBar {
    //设置颜色，默认根据主题颜色设定
    private var statusBarColor: Int = android.R.color.black
    //是否隐藏状态栏，用于全屏播放
    private var statusBarVisible: Boolean = false

    override fun setStatusBarColor(color: Int) {
        this.statusBarColor = color
    }

    override fun setStatusBarVisible(b: Boolean) {
        statusBarVisible = b
    }

    //设置状态栏
    override fun upImmersionBar() {
       val immersionBar   = ImmersionBar.with(activity)
        //如果全屏播放不用设置状态栏颜色
        if (statusBarVisible){
            immersionBar.fullScreen(true) //启用全屏模式
            immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
        }else{
            immersionBar.statusBarColor(statusBarColor)//设置状态栏颜色
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)   // 隐藏虚拟导航栏
        }
        immersionBar.init()
    }
}