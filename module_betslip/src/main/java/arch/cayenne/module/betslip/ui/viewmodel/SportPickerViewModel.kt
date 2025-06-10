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

    private var pendingSelectedId: IntArray? = null

    init {
        viewModelScope.launch {
            val sports = repo.getAllSports()
            pendingSelectedId?.let {
                sports.forEach { sport ->
                    sport.isSelected = it.contains(sport.sportId)
                }
            }
            _onSportListener.value = sports
        }
    }

    fun setSelectedById(id: Int) {
        val current = _onSportListener.value
        if (current == null) {
            // 尚未載入完，先暫存選擇 ID
            pendingSelectedId = listOf(id).toIntArray()
            return
        }
        val defaultId = SportFilterBean.ALL_TYPE_ID
        _onSportListener.value = if (id == defaultId) {
            current.map { sport ->
                if (sport.sportId == defaultId)  {
                    sport.copy(isSelected = true)
                } else {
                    sport.copy(isSelected = false)
                }
            }
        } else {
            current.map { sport ->
                when (sport.sportId) {
                    defaultId -> sport.copy(isSelected = false)
                    id -> sport.copy(isSelected = !sport.isSelected)
                    else -> sport
                }
            }
        }


    }

    fun setSelectedById(ids: IntArray) {
        val current = _onSportListener.value
        if (current == null) {
            // 尚未載入完，先暫存選擇 ID
            pendingSelectedId = ids
            return
        }
        if (ids.size == 1) {
            setSelectedById(ids[0])
        } else {
            _onSportListener.value = current.map { sport ->
                sport.copy(isSelected = ids.contains(sport.sportId))
            }
        }
    }

    fun getSelectedSportBean(): List<SportFilterBean> {
        return _onSportListener.value?.filter { it.isSelected } ?: listOf(SportFilterBean.getAllTypeBean())
    }

    override fun reset() {
        setSelectedById(intArrayOf(-1))
    }
}