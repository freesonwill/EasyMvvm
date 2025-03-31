package com.walisport.lib_base.ui

import android.app.Activity
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

    override fun setStatusBarColor(color: Int) {
        this.statusBarColor = color
        ImmersionBar.with(activity).statusBarColor(statusBarColor).init()
    }

    override fun setStatusBarVisible(b: Boolean) {

    }
}