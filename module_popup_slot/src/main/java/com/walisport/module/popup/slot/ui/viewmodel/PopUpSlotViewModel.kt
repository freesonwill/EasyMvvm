package com.walisport.module.popup.slot.ui.viewmodel

import androidx.lifecycle.LiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.popup.slot.data.PopupSlotBean
import com.walisport.module.popup.slot.data.PopupSlotRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class PopUpSlotViewModel(val repository: PopupSlotRepository) : BaseViewModel() {

    val popupSlotDataListLiveData: LiveData<List<PopupSlotBean>> =
        repository.popupSlotDataListLiveData

    val popupShowLiveData: LiveData<List<Boolean>> =
        repository.popUpShowLiveData

    override fun initViewModel() {
        super.initViewModel()
        repository.getPopupSlotData()
    }


    fun hideView(id: Int) {
        repository.hidePopupSlot(id)
    }


}