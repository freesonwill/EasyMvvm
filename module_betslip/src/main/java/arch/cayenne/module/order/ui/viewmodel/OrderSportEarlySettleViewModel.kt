package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.BetSlipOrderBean
import kotlinx.coroutines.launch

class OrderSportEarlySettleViewModel: BaseViewModel()