package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.order.data.constants.GamePageEnum
import arch.cayenne.module.order.data.constants.OrderSortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderGameViewModel : BaseViewModel() {

    private val _tabType = MutableLiveData<GamePageEnum>()
    val tabType: LiveData<GamePageEnum> get() = _tabType

    private val _sortType = MutableLiveData<OrderSortType>()
    val sortType: LiveData<OrderSortType> get() = _sortType

    fun setTabType(type: GamePageEnum) {
        _tabType.value = type
    }

    fun setSortType(type: OrderSortType) {
        _sortType.value = type
    }
}