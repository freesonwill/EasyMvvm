package com.walisport.module.live.utils

import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import com.walisport.module.live.data.model.LiveBetSlipSelectionData

interface LiveBetSlipItemManagerInterface {
    fun createViewHolder()

    fun covertPlus(
        position: Int,
        count: Int,
        expandedEnum: LiveBetSlipExpandedEnum,
        item: LiveBetSlipSelectionData
    )
}