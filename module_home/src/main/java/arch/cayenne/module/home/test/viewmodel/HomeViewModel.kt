package arch.cayenne.module.home.test.viewmodel

import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import plugin.koin.KoinViewModel

/**
 * @date: 2025/6/10 15:21
 * @description:
 */
@KoinViewModel
class HomeViewModel: BaseViewModel(){
    val number = MutableLiveData(1)

}