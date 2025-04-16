package arch.cayenne.module.home.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.LogUtilsExt.loge
import arch.cayenne.module.home.enums.PlayType
import arch.cayenne.module.home.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class TodayViewModel: BasePlayTypeViewModel() {
    override val playType = PlayType.TODAY

}