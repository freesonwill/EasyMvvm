package arch.cayenne.module.betslip.data.model

import galaxy.common.proto.Common

sealed class BetSlipSelectionData

data class BetSlipOrderSelectionData(val selection: Common.OrderSelection) : BetSlipSelectionData()
data class BetSlipReserveSelectionData(val reserve: Common.ReserveOrderSelection) : BetSlipSelectionData()