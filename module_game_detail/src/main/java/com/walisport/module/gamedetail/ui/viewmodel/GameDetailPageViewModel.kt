package com.walisport.module.gamedetail.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

@KoinViewModel
class GameDetailPageViewModel : BaseViewModel() {
    // Shared state for Carousel index
    val sharedCarouselIndex = MutableLiveData(0)
}
