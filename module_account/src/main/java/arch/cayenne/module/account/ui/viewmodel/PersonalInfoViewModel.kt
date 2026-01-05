package arch.cayenne.module.account.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.entity.UserDataBean
import arch.cayenne.module.account.data.model.PersonalInfoData
import arch.cayenne.module.account.data.repo.PersonalInfoRepository
import org.koin.core.component.inject
import plugin.koin.KoinViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * @author: ricky.chang
 * @date: 2025/6/9 下午4:02
 * @description:
 */
@KoinViewModel
class PersonalInfoViewModel : BaseViewModel() {

    private val repository: PersonalInfoRepository by inject()
    private val _onUserInfoListener = MutableLiveData<UserDataBean>()
    val onUserInfoListener: LiveData<UserDataBean> get() = _onUserInfoListener


    private val _showAvatar = MutableLiveData<String>()
    val showAvatar: LiveData<String> = _showAvatar


    override fun initViewModel() {
        super.initViewModel()
        viewModelScope.launch {
            repository.observeUserInfo().collect {
                _onUserInfoListener.value = it
            }
        }
    }

    fun setUploadResul(filePath: String) {

        _showAvatar.value = filePath
    }

    //获取账户信息
    fun getAccountInfo() {
        viewModelScope.launch {
            repository.getAccountInfo()
        }
    }

}