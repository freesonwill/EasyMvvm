package arch.cayenne.module.bet.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BetCombViewModel : BaseViewModel() {
    private val _isExpandFlow = MutableStateFlow(ExpandState.COLLAPSED) //是否已经展开
    var isExpandFlow: Flow<ExpandState> = _isExpandFlow.asStateFlow()


    var isExpand: ExpandState
        get() = _isExpandFlow.value
        set(value) = run { _isExpandFlow.value = value }

    enum class ExpandState {
        EXPANDED,
        EXPANDING,
        COLLAPSING,
        COLLAPSED
    }
}