package com.walisport.module.live.data.model

import galaxy.common.proto.Common

data class LiveBetSlipSelectionData(val selection:Common.OrderSelection? = null,val reserve:Common.ReserveOrderSelection? = null) {
}