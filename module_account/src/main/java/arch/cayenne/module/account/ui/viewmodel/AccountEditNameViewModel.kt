package arch.cayenne.module.account.ui.viewmodel

import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import plugin.koin.KoinViewModel
import org.koin.core.component.inject
@KoinViewModel
class AccountEditNameViewModel : BaseViewModel() {
    private val repository: PersonalInfoRepository by inject()


    fun changeNickname(nickName: String) : UnPeekLiveData<Boolean>{
       return repository.changeNickname(nickName)
    }

    fun getAccountNicknameRecommendations() : UnPeekLiveData<List<String>>{
        return repository.getAccountNicknameRecommendations()
    }
}