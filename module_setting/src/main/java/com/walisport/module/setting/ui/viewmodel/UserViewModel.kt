package com.walisport.module.setting.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.setting.data.UserBean
import plugin.koin.KoinViewModel

@KoinViewModel
class UserViewModel : BaseViewModel() {

    private val _userData = MutableLiveData<List<UserBean>>()
    val userData: LiveData<List<UserBean>> = _userData

    fun getUserData() {
        val tmp1 = UserBean(0, "", "用戶_QQu9", "", true)
        val tmp2 = UserBean(1, "", "用戶_QQu7", "", false)
        val tmp3 = UserBean(2, "", "用戶_QQu6", "", false)
        val tmp4 = UserBean(-1, "", "登录其他帐号", "", false)
        _userData.value = listOf(tmp1, tmp2, tmp3, tmp4)
    }

    fun deleteUser(id: Int) {
        val list = _userData.value!!.toMutableList().filter { it.id != id }
        _userData.value = list
    }

    fun selectUser(id: Int) {
        val list = _userData.value!!.toMutableList()
        list.forEach { item ->
            item.isSelected = item.id == id
        }
        _userData.value = list
    }
}