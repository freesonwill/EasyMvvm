package com.walisport.module.search.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

@KoinViewModel
class SearchDatePickerViewModel: BaseViewModel() {
    private var _isMaskClickable = true
    val isMaskClickable: Boolean
        get() = _isMaskClickable

    fun setMaskClickable(clickable: Boolean) {
        _isMaskClickable = clickable
    }
}