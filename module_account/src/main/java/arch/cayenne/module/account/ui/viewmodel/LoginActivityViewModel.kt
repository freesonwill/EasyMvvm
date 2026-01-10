package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import arch.cayenne.module.account.data.repo.LoginActivityRepository
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 *
 * @date: 2026/1/10 17:27
 * @description:
 */
class LoginActivityViewModel : BaseActivityViewModel() {
    private val repository: LoginActivityRepository by inject { parametersOf(viewModelScope) }
    fun getSkinType(): String {
        return repository.getSkinType()
    }

    override val shouldBeAutoLogin: Boolean
        get() = false

}