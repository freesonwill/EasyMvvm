package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.repository.HomeRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TodayGameListViewModel: BaseGameListViewModel() {
    override val playType: PlayType = PlayType.TODAY

}