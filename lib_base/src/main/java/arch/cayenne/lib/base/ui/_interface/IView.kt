package arch.cayenne.lib.base.ui._interface

import android.os.Bundle
import androidx.lifecycle.Lifecycle

/**
 * @author: zhangsan
 * @date: 2025/3/14 11:52
 * @description:
 */
interface IView : OnNewIntentListener {
    /**
     * 初始化view
     */
    fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化监听器
     */
    fun initListener()

    /**
     * 在那个生命周期状态创建数据观察者
     */
    fun createObserverAtState():Lifecycle.State = Lifecycle.State.CREATED

    /**
     * 创建数据观察者
     */
    fun createObserver()

    /**
     * 初始化数据
     */
    fun initData() {}

    /**
     * 懒加载数据
     */
    fun lazyLoadData() {}

    /**
     * 是否追踪加载时间（DEBUG用）
     */
    fun enableTrackLoadTime() = false
}