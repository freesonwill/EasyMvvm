package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.SystemAvatarBean
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import plugin.koin.KoinViewModel
@KoinViewModel
class SystemAvatarViewModel :  BaseViewModel() {

    private val repository: PersonalInfoRepository by inject()

    private val _systemAvatarList = MutableLiveData<List<SystemAvatarBean>>()
    val systemAvatarList: LiveData<List<SystemAvatarBean>> = _systemAvatarList
    override fun initViewModel() {
        repository.uploadAvatarResult.observeForever { result ->
            _uploadResult.postValue(result)
        }
        super.initViewModel()
    }

    fun getPersonalInfoData() {
        viewModelScope.launch(Dispatchers.IO) {
            _systemAvatarList.postValue(repository.getPersonalInfoData())
        }
    }

    // 用于监听上传结果
    private val _uploadResult = MutableLiveData<String>()
    val uploadResult: LiveData<String> = _uploadResult

    // 上传头像
    fun uploadAvatar(filePath: String) {
        repository.uploadAvatar(
            "100",
            "MTAwXzE3NjU0Mzc1NTk1MDk6ZFBoc3dpelQwazRTaUJnbg",
            filePath
        )
        repository.uploadResult.observeForever { result ->
            repository.updateAvatar(avatarUrl = result, type = "1", avatarId = "")
        }
    }

    fun uploadAvatarUrl(avatarUrl: String) {
        repository.updateAvatar(avatarUrl = avatarUrl, type = "0", avatarId = "")
    }


}