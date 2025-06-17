package arch.cayenne.module.account.ui.viewmodel

import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

/**
 * @author: ricky.chang
 * @date: 2025/6/9 下午4:02
 * @description:
 */
@KoinViewModel
class PersonalInfoViewModel : BaseViewModel() {
    private val repository: PersonalInfoRepository by inject()
    fun getPersonalInfoData(): List<PersonalInfoData> {
        return repository.getPersonalInfoData()
    }
    fun saveData(nickName: String, resId: Int, position: Int) {
        repository.saveData(nickName, resId, position)
        // Optionally, you can also update the UI or notify the user that the data has been saved
    }
    fun getDefaultNickName(): String {
        return repository.getDefaultNickName()
    }
    fun getDefaultPosition(): Int {
        return repository.getDefaultPosition()
    }
}