package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.repo.UnReadMessageRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 *
 * @date: 2025/10/21 16:42
 * @description:
 */
class UnReadMessageViewModel : BaseViewModel() {

    private val repository: UnReadMessageRepository by inject { parametersOf(viewModelScope) }

    private val _unreadMsg = UnPeekLiveData<Boolean>(false)
    val unreadMsg: LiveData<Boolean> get() = _unreadMsg

    fun createObserver() {
        viewModelScope.launch {
            repository.observeUnReadMsg().collect {
                it.let {
                    _unreadMsg.value = it.isNotEmpty()
                }
            }
        }

    }
}