package com.walisport.module.popup.slot.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.popup.slot.data.PopupSlotRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class PopUpSlotViewModel(val repository: PopupSlotRepository) : BaseViewModel() {

    var anchorX: Float
        get() = repository.anchorX
        set(value) {
            repository.anchorX = value
        }

    var anchorY: Float
        get() = repository.anchorY
        set(value) {
            repository.anchorY = value
        }

    override fun initViewModel() {
        super.initViewModel()
    }




}