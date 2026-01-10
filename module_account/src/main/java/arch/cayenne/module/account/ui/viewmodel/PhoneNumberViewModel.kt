package arch.cayenne.module.account.ui.viewmodel

import android.provider.Telephony.Sms
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.model.LoginResponseVo
import arch.cayenne.module.account.data.repo.AccountLoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class PhoneNumberViewModel : BaseViewModel() {

    private val repository: AccountLoginRepository by inject()

    private val _smsState = MutableLiveData<SmsState>()
    val smsState: LiveData<SmsState> = _smsState


    override fun initViewModel() {
        super.initViewModel()
    }

    /**
     * 请求验证码
     */
    fun requestSMSCode(countryCode: String, phoneNumber: String) {
        setState(DataState.Loading)
        viewModelScope.launch {
            delay(3_000) //模拟网络延迟
            setState(DataState.LoadSuccess)
            _smsState.value = SmsState.Success

//            callApi(
//                {
//                    repository.accountLogin(
//                        countryCode = countryCode,
//                        phoneNumber = phoneNumber,
//                        sms = ""
//                    )
//                },
//                {
//                    when (it) {
//                        is ApiResponseState.Failed -> {
//                            setState(DataState.NetworkUnavailable)
//                        }
//
//                        is ApiResponseState.Succeeded<*> -> {
//                            setState(DataState.LoadSuccess)
//
//                            viewModelScope.launch(Dispatchers.IO) {
//                                val loginResponseVo = it.dataAs<LoginResponseVo>()
//                                loginResponseVo?.let { loginResponseVo ->
//                                    if (loginResponseVo.isReg) {
//                                        //进入验证码登录/注册流程
//                                        _smsState.value = SmsState.Success
//                                    } else {
//                                        _smsState.value = SmsState.Failure
//                                    }
//                                }
//                            }
//                        }
//
//                        else -> {}
//                    }
//                }, autoUpdateState = false
//            )
        }
    }


    /**
     * 使用手机号和验证码登录
     */
    fun accountLogin(countryCode: String, phoneNumber: String, sms: String) {
        setState(DataState.Loading)
        viewModelScope.launch {
            callApi(
                {
                    repository.accountLogin(
                        countryCode = countryCode,
                        phoneNumber = phoneNumber,
                        sms = sms
                    )
                },
                {
                    when (it) {
                        is ApiResponseState.Failed -> {
                            setState(DataState.NetworkUnavailable)
                        }

                        is ApiResponseState.Succeeded<*> -> {
                            setState(DataState.LoadSuccess)

                            viewModelScope.launch(Dispatchers.IO) {
                                val loginResponseVo = it.dataAs<LoginResponseVo>()
                            }
                        }

                        else -> {}
                    }
                }, autoUpdateState = false
            )
        }
    }

}

sealed class SmsState : DataState {
    data object Success : SmsState()
    data object Failure : SmsState()
}