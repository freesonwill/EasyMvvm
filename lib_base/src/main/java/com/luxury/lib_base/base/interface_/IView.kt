package com.luxury.lib_base.base.interface_

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

interface IView {
    /**
     * View初始化
     */
    fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化状态栏
     */
    fun initStatusBar()

    /**
     * 注册LiveData的观察者
     */
    fun initObserver()

    /**
     * ViewModel的构造非无参构造，需要听Factory
     */
    fun provideViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }
}