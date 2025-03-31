package com.walisport.lib_base.ui.interface_

/**
 * @author: zhangsan
 * @date: 2025/3/31 14:24
 * @description:
 */
interface IStatusBar {
    /**
     * 设置状态栏
     */
    fun setStatusBar(config: Config)

    /**
     * 状态栏配置
     * @property statusBarColor
     * @property statusBarVisible
     */
    data class Config(
        val statusBarColor: Int = android.R.color.transparent,
        val statusBarVisible: Boolean = true,
    )
}
