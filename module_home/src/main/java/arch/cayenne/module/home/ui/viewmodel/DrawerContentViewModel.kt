package arch.cayenne.module.home.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.skin.SkinnableManager
import arch.cayenne.module.home.data.repo.DrawerContentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import plugin.koin.KoinViewModel

@KoinViewModel
class DrawerContentViewModel: BaseViewModel() {
    // TODO:("通知API待連接")
    private val repository: DrawerContentRepository by inject()
    private val skinManager: SkinnableManager by inject { parametersOf(viewModelScope) }
    private val _selectedSkinType = MutableLiveData<Event<String>>()
    val selectedSkinType: LiveData<Event<String>> = _selectedSkinType
    init {
       viewModelScope.launch {
            skinManager.skinFlow.collect {
                _selectedSkinType.value = Event(it)
            }
        }
    }
    fun getDefaultResId(): Int {
        return repository.getDefaultResId()
    }
    fun getDefaultNickName(): String {
        return repository.getDefaultNickName()
    }
}