package com.walisport.module.topup.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.TopUpMainRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class TopUpMainViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    override fun initViewModel() {
        super.initViewModel()
    }


}