package arch.cayenne.lib.base.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

/**
 * @author: zhangsan
 * @date: 2025/3/14 09:51
 * @description:
 */
abstract class BaseViewModel : ViewModel(), KoinComponent {

    protected val TAG = this.javaClass.simpleName

    private val _apiStateListener = MutableLiveData<DataState>()
    val apiStateListener: LiveData<DataState> get() = _apiStateListener

    //如果有需要的話，把一些相關的元件設定回初始狀態
    open fun reset() {}

    open fun initViewModel() {}

    protected fun callApi(api: suspend () -> ApiResponseState, handle: ((ApiResponseState) -> Unit)? = null, autoUpdateState: Boolean = true) {
        if (autoUpdateState) {
            _apiStateListener.value = DataState.None
        }
        handle?.invoke(ApiResponseState.Start)
        val jobs = viewModelScope.async {
            api()
        }
        viewModelScope.launch {
            if (autoUpdateState) {
                _apiStateListener.value = DataState.Loading
            }
            handle?.invoke(ApiResponseState.Processing)
            val response = jobs.await()
            if (autoUpdateState) {
                if (response is ApiResponseState.Failed) {
                    _apiStateListener.value = DataState.NetworkUnavailable
                } else if (response is ApiResponseState.Succeeded<*>) {
                    val data = response.data
                    if (data is List<*> && data.isEmpty()) {
                        _apiStateListener.value = DataState.DataEmpty
                    } else {
                        _apiStateListener.value = DataState.LoadSuccess
                    }
                }
            }
            handle?.invoke(response)
        }
    }

    protected fun setState(state: DataState) {
        _apiStateListener.value = state
    }

}