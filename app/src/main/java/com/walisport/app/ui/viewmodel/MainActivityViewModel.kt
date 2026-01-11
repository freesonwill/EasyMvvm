package com.walisport.app.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.ui.viewmodel.BaseActivityViewModel
import com.walisport.app.data.repo.MainRepository
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 * @author: zhangsan
 * @date: 2025/3/14 16:56
 * @description:
 */
class MainActivityViewModel : BaseActivityViewModel() {
    private val repository: MainRepository by inject { parametersOf(viewModelScope) }
    override val shouldBeAutoLogin: Boolean = true

    private val userDataManager: UserDataManager by inject()

    override fun initViewModel() {
        super.initViewModel()
        //要给token flow赋初值
        userDataManager.notifyToken()

        viewModelScope.launch {
            repository.observeUserToken()
                .filter { it }
                .collect {
                repository.loadSportList()
                repository.observeSystemNotify()
            }
        }

    }

    fun getSkinType(): String {
        return repository.getSkinType()
    }

}