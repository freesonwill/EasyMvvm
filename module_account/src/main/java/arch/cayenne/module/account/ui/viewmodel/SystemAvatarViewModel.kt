package arch.cayenne.module.account.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel
@KoinViewModel
class SystemAvatarViewModel :  BaseViewModel() {

    private val repository: PersonalInfoRepository by inject()
    fun getPersonalInfoData(): List<PersonalInfoData> {
        return repository.getPersonalInfoData()
    }
    fun saveData(nickName: String, resId: Int, position: Int) {
        repository.saveData(nickName, resId, position)
    }
    fun getDefaultNickName(): String {
        return repository.getDefaultNickName()
    }
    fun getDefaultPosition(): Int {
        return repository.getDefaultPosition()
    }
}