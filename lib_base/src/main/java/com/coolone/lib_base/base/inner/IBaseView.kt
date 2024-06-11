package com.coolone.lib_base.base.inner

import android.os.Bundle
import android.view.View

interface IBaseView {

    fun initView(savedInstanceState: Bundle?) {}

    fun onBackPressed()

    /**
     * 对ViewModel的数据监听
     */
    fun initObserver()
}