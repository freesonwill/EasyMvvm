package arch.cayenne.module.order.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.data.model.SportFilterBean
import arch.cayenne.module.order.data.model.OrderAllBean
import kotlinx.coroutines.launch

class GameFilterViewModel : BaseViewModel() {

    private val _onSportListener = MutableLiveData<List<SportFilterBean>>()
    val onSportListener: LiveData<List<SportFilterBean>> get() = _onSportListener

    init {
        viewModelScope.launch {
            val tmp0 = SportFilterBean(
                0,
                "全部",
                true
            )
            val tmp1 = SportFilterBean(
                1,
                "视讯",
                false
            )
            val tmp2 = SportFilterBean(
                2,
                "老虎机",
                false
            )
            val tmp3 = SportFilterBean(
                3,
                "棋牌",
                false
            )
            val tmp4 = SportFilterBean(
                4,
                "捕鱼",
                false
            )
            val tmp5 = SportFilterBean(
                5,
                "彩票",
                false
            )
            val tmp6 = SportFilterBean(
                6,
                "电竞",
                false
            )
            _onSportListener.value = listOf(tmp0, tmp1, tmp2, tmp3, tmp4, tmp5, tmp6)
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
                if (sport.sportId == defaultId) {
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

    }
}