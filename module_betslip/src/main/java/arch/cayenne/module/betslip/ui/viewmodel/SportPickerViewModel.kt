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
            repo.observeSportFlow.collect {
                val current = _onSportListener.value
                if (current != null) {
                    // 如果存在旧数据，需要合并选中状态
                    // 创建一个映射，方便通过 sportId 查找旧数据的选中状态
                    val oldSelectionMap = current.associateBy(
                        { it.sportId }, // Key: sportId
                        { it.isSelected } // Value: isSelected
                    )

                    // 遍历新的列表，更新其 isSelected 状态
                    val updatedNewList = it.map { newItem ->
                        // 尝试从旧的选中状态映射中获取当前 newItem 的选中状态
                        // 如果旧数据中没有这个 sportId (可能是新增的运动)，则默认不选中 (或者根据您的业务逻辑决定)
                        val wasSelected = oldSelectionMap[newItem.sportId] ?: false // 默认 false
                        newItem.copy(isSelected = wasSelected)
                    }
                    _onSportListener.value = updatedNewList
                } else {
                    _onSportListener.value = it
                }
            }
        }
    }

    fun setSelectedById(id: Int) {
        val current = _onSportListener.value ?: return
        val defaultId = SportFilterBean.ALL_TYPE_ID
        val lastSelectedId = current.filter { it.isSelected }
        if (lastSelectedId.size == 1 && lastSelectedId[0].sportId == id && id != defaultId) {
            return
        }

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
        val current = _onSportListener.value ?: return
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