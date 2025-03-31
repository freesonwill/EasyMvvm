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

    override fun setStatusBar(config: IStatusBar.Config) {
        val immersionBar = ImmersionBar.with(activity)
        //如果全屏播放不用设置状态栏颜色
        if (config.hideStatusBar) {
            immersionBar.fullScreen(true) //启用全屏模式
            immersionBar.hideBar(BarHide.FLAG_HIDE_BAR) //状态栏隐藏
        } else {
            immersionBar.statusBarColor(config.statusBarColor)//设置状态栏颜色
                .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)   // 隐藏虚拟导航栏
        }
        immersionBar.init()
    }
}