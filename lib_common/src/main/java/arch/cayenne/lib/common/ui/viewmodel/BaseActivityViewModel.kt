package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.AppNotifyBean
import arch.cayenne.lib.common.data.constants.LoginEnum
import arch.cayenne.lib.common.data.repo.CommonRepository
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.lib.websocket.data.ConnectState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * 這個ViewModel主要處理一些共通監聽的是像，例如統一監聽斷線後重新連線與登入狀態
 * 現在需要做全背景監聽的只有SplashActivity和AppNavActivity
 * */
abstract class BaseActivityViewModel : BaseViewModel() {
    private val commonRepository: CommonRepository by inject { parametersOf(viewModelScope) }

    private val _loginResult = MutableLiveData<LoginEnum>()
    val loginResult: LiveData<LoginEnum> = _loginResult
    private val _betResultListener = MutableLiveData<List<BetResultLiteBean>>()
    val betResultListener: LiveData<List<BetResultLiteBean>> get() = _betResultListener

    //APP通知消息
    private val _appNotifyListener = MutableLiveData<AppNotifyBean>()
    val appNotifyListener: LiveData<AppNotifyBean> get() = _appNotifyListener

    private val _aberrantNotify = MutableLiveData<Int>()
    val aberrantNotify : LiveData<Int> = _aberrantNotify

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                commonRepository.getConnectStateFlow().collect { connectState ->
                    when (connectState) {
                        is ConnectState.ConnectSuccess -> {
                            "Connection Success".logi(BaseActivityViewModel::class.java.simpleName)
                            login()
                        }
                        is ConnectState.ConnectFailure, ConnectState.NetworkUnavailable -> {
                            "Connection Failure -> $connectState".loge(BaseActivityViewModel::class.java.simpleName)
                            commonRepository.setIsLogin(false)
                        }
                        else -> Unit
                    }
                }
            }
            launch {
                commonRepository.observeAppNotifyChange().collect { result ->
                    if (result.error == null && result.data != null) {
                        result.data?.let {
                            val notify = AppNotifyBean(it.type, it.sportId, it.matchId, it.title, it.content)
                            _appNotifyListener.postValue(notify)
                        }
                    }
                }
            }
            launch {
                commonRepository.getBetResultFlow().collect {
                    _betResultListener.postValue(it)
                }
            }
            launch {
                commonRepository.observeBalanceChange()
            }
            launch {
                commonRepository.observeBettingOrderStatus()
            }
            launch {
                commonRepository.observeAberrantNotify().collect {notify ->
                    if (notify.error == null && notify.data != null) {
                        withContext(Dispatchers.Main){
                            _aberrantNotify.value = notify.data!!.code
                        }
                    }
                }
            }
        }
    }

    //當連線成功時，自動地去做補登入
    private fun login() {
        viewModelScope.launch {
            if (commonRepository.checkIsLogin()) {
                _loginResult.value = LoginEnum.SUCCESSFUL
                return@launch
            }
            callApi({
                commonRepository.sendLogin()
            }, {
               if (it is ApiResponseState.Succeeded<*>) {
                   val result = it.dataAs<Boolean>()
                   if (result == null) {
                       "Login is failure! api response parse failed!".loge(TAG)
                       _loginResult.value = LoginEnum.API_FAILURE
                   } else {
                       _loginResult.value = if (result) LoginEnum.SUCCESSFUL else LoginEnum.NOT_SUCCESSFUL
                   }

               } else if (it is ApiResponseState.Failed){
                   "Login is failure! msg = ${it.error?.msg}".loge(TAG)
                   _loginResult.value = LoginEnum.API_FAILURE
               }
            }, false)
        }
    }

    fun reconnectNow() {
        commonRepository.reconnectNow()
    }

    override fun reset() {
        viewModelScope.launch(Dispatchers.IO) {
            commonRepository.reset()
        }

    }
}