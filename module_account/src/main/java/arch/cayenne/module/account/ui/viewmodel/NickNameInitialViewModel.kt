package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class NickNameInitialViewModel : BaseViewModel() {

    val maxInputLength: Int = 12

    private val repository: PersonalInfoRepository by inject()

    fun changeNickname(nickName: String) : UnPeekLiveData<Boolean>{
        return repository.changeNickname(nickName)
    }

    //获取账户信息
    fun getAccountInfo() {
        viewModelScope.launch {
            repository.getAccountInfo()
        }
    }


}