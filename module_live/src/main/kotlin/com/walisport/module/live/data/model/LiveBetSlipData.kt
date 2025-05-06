package com.walisport.module.live.data.model

import com.walisport.module.live.data.constants.LiveBetSlipExpandedEnum
import galaxy.common.proto.Common

data class LiveBetSlipData(val order:Common.Order? = null,val reserve:Common.ReserveOrder? = null,var expandedEnum: LiveBetSlipExpandedEnum = LiveBetSlipExpandedEnum.Hide)
