package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logi
import arch.cayenne.lib.common.data.constants.AppNotifyBean
import arch.cayenne.lib.common.data.repo.CommonRepository
import arch.cayenne.lib.database.entity.BetResultLiteBean
import arch.cayenne.lib.websocket.data.ConnectState
import arch.cayenne.lib.websocket.data.SocketResponseError
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

    // 每個activity針對登入和離線錯誤都有不同的處理，接收到相對應的livedata後各自處理
    val loginIsSuccess = MutableLiveData<Boolean>()
    val loginError = MutableLiveData<SocketResponseError>()
    private val _betResultListener = MutableLiveData<List<BetResultLiteBean>>()
    val betResultListener: LiveData<List<BetResultLiteBean>> get() = _betResultListener

    private val _connectFailed = MutableLiveData<ConnectState>()
    val connectFailed : LiveData<ConnectState> = _connectFailed

    //APP通知消息
    private val _appNotifyListener = MutableLiveData<AppNotifyBean?>()
    val appNotifyListener: LiveData<AppNotifyBean?> get() = _appNotifyListener

    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch(Dispatchers.IO) {
            commonRepository.getConnectStateFlow().collect { connectState ->
                "KC_ connectState = $connectState".logi()
                when (connectState) {
                    is ConnectState.ConnectSuccess -> {
                        "Connection Success".logi(BaseActivityViewModel::class.java.simpleName)
                        withContext(Dispatchers.Main) {
                            _connectFailed.value = connectState
                        }
                        login()
                    }
                    is ConnectState.ConnectFailure, ConnectState.NetworkUnavailable -> {
                        "Connection Failure -> $connectState".loge(BaseActivityViewModel::class.java.simpleName)
                        commonRepository.tryToReconnect()
                        withContext(Dispatchers.Main) {
                            _connectFailed.value = connectState
                        }

                    }
                    is ConnectState.ReconnectFailure -> {
                        withContext(Dispatchers.Main) {
                            _connectFailed.value = connectState
                        }
                    }
                    else -> Unit
                }
            }
            commonRepository.observeAppNotifyChange().collect { result ->
                if (result.error == null && result.data != null) {
                    val temp = result.data?.let {
                        AppNotifyBean(it.type, it.sportId, it.title, it.content)
                    }
                    _appNotifyListener.value = temp
                }
            }
            launch(Dispatchers.IO) {
                commonRepository.getBetResultFlow().collect {
                    _betResultListener.postValue(it)
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                commonRepository.observeBalanceChange()
            }
            launch {
                commonRepository.observeBettingOrderStatus()
            }
        }
    }

    //當連線成功時，自動地去做補登入
    private fun login() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = commonRepository.sendLogin()
            withContext(Dispatchers.Main) {
                when (result.error) {
                    null -> {
                        "Login  Is Success? = ${result.data?.success}".logi(this@BaseActivityViewModel::class.java.simpleName)
                        loginIsSuccess.value = result.data?.success == true
                    }
                    else -> {   //其餘錯誤
                        loginError.value = result.error!!
                    }
                }
            }
        }
    }

    fun reconnectNow() {
        commonRepository.reconnectNow()
    }

    override fun reset() {
        commonRepository.reset()
    }
}