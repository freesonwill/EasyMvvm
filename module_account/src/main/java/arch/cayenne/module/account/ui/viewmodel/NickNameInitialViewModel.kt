package arch.cayenne.module.account.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.repo.AccountLoginRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class NickNameInitialViewModel :  BaseViewModel() {

    private val repository: AccountLoginRepository by inject()

    override fun initViewModel() {
        super.initViewModel()
    }

}