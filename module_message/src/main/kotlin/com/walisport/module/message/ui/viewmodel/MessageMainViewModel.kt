package com.walisport.module.message.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.message.data.MessageMainRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class MessageMainViewModel(private val repo: MessageMainRepository) : BaseViewModel() {

    override fun initViewModel() {
        super.initViewModel()
    }


}