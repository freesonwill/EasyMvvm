package com.walisport.module.feedback.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.feedback.data.FeedbackMainRepository
import plugin.koin.KoinViewModel

@KoinViewModel
class FeedbackMainViewModel(private val repo: FeedbackMainRepository) : BaseViewModel() {

    val maxInputLength = 200

    override fun initViewModel() {
        super.initViewModel()
    }


}