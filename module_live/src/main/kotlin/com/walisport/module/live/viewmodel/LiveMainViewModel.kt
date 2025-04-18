package com.walisport.module.live.viewmodel

import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.LiveMainRepository

class LiveMainViewModel(private val repo: LiveMainRepository) : BaseViewModel() {
    fun liveStream(matchId: Int) {
        repo.liveStream(matchId)
    }
}