package arch.cayenne.module.account.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.repo.LoginOrRegisterRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class LoginOrRegisterViewModel :  BaseViewModel() {

    private val repository: LoginOrRegisterRepository by inject()


    override fun initViewModel() {
        super.initViewModel()
    }

}