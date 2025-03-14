package com.walisport.lib_base.ui.interface_

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
     * 初始化监听器
     */
    fun initListener()

    /**
     * 初始化数据
     */
    fun initData() {}

    /**
     * 创建数据观察者
     */
    fun createObserver()

    /**
     * 延迟加载时间 防止 切换动画还没执行完毕时数据就已经加载好了，这时页面会有渲染卡顿  bug
     * 这里传入你想要延迟的时间，延迟时间可以设置比转场动画时间长一点 单位： 毫秒
     * 不传默认 300毫秒
     * @return Long
     */
    fun lazyLoadTime(): Long {
        return 300
    }

    /**
     * 延迟加载数据
     */
    fun lazyLoadData()

}