package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.constants.NicknameInitialState
import arch.cayenne.module.account.data.constants.SmsVerifyState
import arch.cayenne.module.account.data.model.LoginResponseVo
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class NickNameInitialViewModel : BaseViewModel() {

    val maxInputLength: Int = 12

    private val repository: PersonalInfoRepository by inject()

    fun changeNickname(nickName: String) {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.changeNicknameResponse(nickName)
                },
                {
                    when (it) {
                        is ApiResponseState.Succeeded<*> -> {
                            setState(NicknameInitialState.Success)
                        }

                        is ApiResponseState.Failed -> {
                            setState(NicknameInitialState.Failure)
                        }

                        else -> {}
                    }
                }, autoUpdateState = false
            )
        }
    }


}