package com.walisport.module.live.utils

import com.walisport.module.live.data.model.LiveBetSlipData

interface LiveBetSlipAdapterMangerInterface {
    fun createViewHolder()

    fun covertPlus(position: Int, item: LiveBetSlipData)
}