package com.walisport.lib_base.ui.interface_

/**
 * 状态栏StatusBar
 * @author: zhangsan
 * @date: 2025/3/31 14:24
 * @description:
 */
interface IStatusBar {
    /**
     * 配置StatusBar
     */
    fun configStatusBar(): StatusBarConfig = StatusBarConfig()

    /**
     * 设置状态栏
     */
    fun setStatusBar(config: StatusBarConfig)
}

/**
 * 状态栏配置
 * @property statusBarColor 状态栏颜色
 * @property hideStatusBar 是否隐藏状态栏
 */
data class StatusBarConfig(
    //状态栏颜色
    val statusBarColor: Int = android.R.color.black,
    //是否隐藏状态栏
    val hideStatusBar: Boolean = false,
)

