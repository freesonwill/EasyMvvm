package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel

@KoinViewModel
class AvatarPreviewViewModel : BaseViewModel() {

    private val repository: PersonalInfoRepository by inject()

    override fun initViewModel() {
        repository.uploadAvatarResult.observeForever { result ->
            _uploadResult.postValue(result)
        }
        super.initViewModel()
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
            repository.updateAvatar(avatarUrl = result, type = "1", avatarId = -1)
        }
    }
}