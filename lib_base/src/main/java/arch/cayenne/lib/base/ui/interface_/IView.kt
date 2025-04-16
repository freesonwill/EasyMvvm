package arch.cayenne.lib.base.ui.interface_

import android.os.Bundle

/**
 * @author: zhangsan
 * @date: 2025/3/14 11:52
 * @description:
 */
interface IView {
    /**
     * 初始化view
     */
    fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    fun initData() {}

    /**
     * 初始化监听器
     */
    fun initListener()

    /**
     * 创建数据观察者
     */
    fun createObserver()

    /**
     * 是否追踪加载时间（DEBUG用）
     */
    fun enableTrackLoadTime() = false
}