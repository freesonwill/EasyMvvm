package com.coolone.lib_base.base.inner

import android.os.Bundle

interface IBaseView {

    fun initView(savedInstanceState: Bundle?) {}

    fun initData()

    fun onBackPressed()

    fun initObserver()
}