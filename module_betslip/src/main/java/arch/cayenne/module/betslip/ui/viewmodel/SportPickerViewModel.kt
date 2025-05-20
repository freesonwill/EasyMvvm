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

    private var pendingSelectedId: Int? = null

    init {
        viewModelScope.launch {
            val sports = repo.getAllSports()
            _onSportListener.value = sports

            // 如果初始化之前有選取請求，就處理它
            pendingSelectedId?.let {
                setSelectedById(it)
            }
        }
    }

    fun setSelectedById(id: Int) {
        val current = _onSportListener.value
        if (current == null) {
            // 尚未載入完，先暫存選擇 ID
            pendingSelectedId = id
            return
        }
        _onSportListener.value?.let { data ->
            _onSportListener.value = if (id == -1) {
                data.map { it.copy(isSelected = false) }
            } else {
                data.map { it.copy(isSelected = it.sportId == id) }
            }
        }
    }

    fun getSelectedSportId(): Int {
        return _onSportListener.value?.find { it.isSelected }?.sportId ?: -1
    }
}