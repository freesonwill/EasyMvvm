package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.order.data.constants.GamePageEnum

class OrderGameViewModel : BaseViewModel() {

    private val _tabType = MutableLiveData<GamePageEnum>()
    val tabType: LiveData<GamePageEnum> get() = _tabType

    fun setTabType(type: GamePageEnum) {
        _tabType.value = type
    }
}