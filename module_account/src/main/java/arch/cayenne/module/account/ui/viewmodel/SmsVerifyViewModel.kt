package arch.cayenne.module.account.ui.viewmodel

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
class SmsVerifyViewModel : BaseViewModel() {

    private val repository: AccountLoginRepository by inject()

    private val countDownLiveData = MutableLiveData<Int>()
    fun getCountDownLiveData(): LiveData<Int> = countDownLiveData

    override fun initViewModel() {
        super.initViewModel()
    }

    fun startCountDown() {
        var seconds = 60
        countDownLiveData.value = seconds
        viewModelScope.launch {
            while (seconds > 0) {
                delay(1000)
                seconds--
                countDownLiveData.postValue(seconds)
            }
        }
    }

    /**
     * 请求验证码
     */
    fun requestSMSCode(countryCode: String, phoneNumber: String) {
        viewModelScope.launch {
            delay(1_500) //模拟网络延迟
        }
    }

    fun verifySmsCode(countryCode: String, phoneNumber: String, smsCode: String) {
        accountLogin(countryCode, phoneNumber, smsCode)
    }

    /**
     * 使用手机号和验证码登录
     */
    private fun accountLogin(countryCode: String, phoneNumber: String, sms: String) {
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