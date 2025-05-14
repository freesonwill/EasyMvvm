package arch.cayenne.module.betslip.data.model

import galaxy.common.proto.Common

data class LiveBetSlipData(val order:Common.Order? = null,val reserve:Common.ReserveOrder? = null,var expandedEnum: arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum = arch.cayenne.module.betslip.data.constants.LiveBetSlipExpandedEnum.Hide)
