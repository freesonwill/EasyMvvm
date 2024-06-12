package com.luxury.lib_base.base

import androidx.lifecycle.ViewModel
import com.luxury.lib_base.base.interface_.IModel

abstract class BaseViewModel : ViewModel(), IModel {

    override fun onCleared() {
        super.onCleared()
    }

}