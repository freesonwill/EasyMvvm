package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xcjh.base_lib2.base.BaseViewModel

class WinningAnimationViewModel: BaseViewModel() {

    private val _resultVisible = MutableLiveData(false)
    val resultVisible: LiveData<Boolean> = _resultVisible

    var isAnimating = false

    fun setResultVisible(isVisible: Boolean) {
        _resultVisible.value = isVisible
    }
}