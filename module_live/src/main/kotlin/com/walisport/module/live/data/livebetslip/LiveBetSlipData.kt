package com.walisport.module.live.data.livebetslip

import galaxy.common.proto.Common

data class LiveBetSlipData(val order:Common.Order? = null,val reserve:Common.ReserveOrder? = null,var expandedEnum: LiveBetSlipExpandedEnum = LiveBetSlipExpandedEnum.Hide)
