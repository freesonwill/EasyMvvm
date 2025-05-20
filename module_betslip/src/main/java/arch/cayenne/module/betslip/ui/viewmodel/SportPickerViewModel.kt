package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.data.model.SportFilterBean
import arch.cayenne.module.betslip.data.repo.SportPickerRepository
import kotlinx.coroutines.launch

class SportPickerViewModel(private val repo: SportPickerRepository): BaseViewModel() {

    private val _onSportListener = MutableLiveData<List<SportFilterBean>>()
    val onSportListener: LiveData<List<SportFilterBean>> get() = _onSportListener

    init {
        viewModelScope.launch {
            _onSportListener.value = repo.getAllSports()
        }
    }

    fun setSelectedById(id: Int) {
        _onSportListener.value?.let { data ->
            _onSportListener.value = if (id == -1 ) {
                data.onEach { it.isSelected = false }
            } else {
                data.onEach { it.isSelected = it.sportId == id }
            }
        }
    }

    fun getSelectedSportId(): Int {
        return _onSportListener.value?.find { it.isSelected }?.sportId ?: -1
    }
}