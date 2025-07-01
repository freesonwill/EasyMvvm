package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.CurConnectFailedType


class ConnectFailedViewModel: BaseViewModel() {
    private val _currencyFailedType = MutableLiveData<CurConnectFailedType>()
    val curConnectFailedType: LiveData<CurConnectFailedType> = _currencyFailedType

    fun changeCurrencyFailedView(type: CurConnectFailedType) {
        _currencyFailedType.value = type
    }
}