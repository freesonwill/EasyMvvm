package com.luxury.lib_base.base.interface_

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider

interface IView {
    fun initView(savedInstanceState: Bundle?)
    fun initObserver()
    fun initViewModelFactory(): ViewModelProvider.Factory? {
        return null
    }
}