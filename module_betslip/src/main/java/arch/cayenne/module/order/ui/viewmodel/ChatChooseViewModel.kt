package arch.cayenne.module.order.ui.viewmodel

import android.os.UserManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.UserDataKey
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.module.order.data.model.BetShareBean
import arch.cayenne.module.order.data.model.ChooseBetData
import arch.cayenne.module.order.data.repo.BetSlipHttpRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

/**
 * @author: wenxi
 * @date: 11/12/25 20:55
 * @description:
 */
class ChatChooseViewModel : BaseViewModel() {
    private val _betClickLiveData: MutableLiveData<ChooseBetData> = MutableLiveData()
    val betClickLiveData: LiveData<ChooseBetData> = _betClickLiveData
    val report: BetSlipHttpRepository by inject { parametersOf(viewModelScope) }
    val betShareLiveData = MutableLiveData<BetShareBean>()
    private val manager: UserDataManager by inject()

    /**
     * type 0 game 1 sport
     * */
    fun clickBtn(type: ChooseBetData) {
        _betClickLiveData.value = type
    }

    fun getBetShare(userId: Long, settleId: String) {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    report.getBetShare(userId, settleId)
                },
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                            setState(DataState.NetworkUnavailable)
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            setState(DataState.LoadSuccess)
                            val data = it.dataAs<BetShareBean>()
                            betShareLiveData.value = data
                        }

                        else -> {}

                    }

                },
                autoUpdateState = false
            )
        }
    }

    fun getUid(): Long {
        return manager.getValue(UserDataKey.KEY_UID, -1)
    }
}