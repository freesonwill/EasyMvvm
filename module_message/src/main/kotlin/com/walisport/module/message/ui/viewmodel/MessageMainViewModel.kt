package com.walisport.module.message.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.data.NotificationBean
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class MessageMainViewModel(private val repo: MessageMainRepository) : BaseViewModel() {

    private val _notificationBean = MutableLiveData<List<NotificationBean>>()
    val notificationBean: LiveData<List<NotificationBean>> = _notificationBean

    private val _notificationSelect = MutableLiveData<List<NotificationBean>>()
    val notificationSelect: LiveData<List<NotificationBean>> = _notificationSelect

    private var msgId: Long = 0L

    //获取用户消息列表
    fun getMessageList() {
        viewModelScope.launch {
            val resp = repo.getUserMessageList(0)
            resp.let {
                if (resp.isNotEmpty()) {
                    msgId = resp.last().id
                }
            }
            _notificationBean.value = resp
        }
    }

    //加载更多用户消息列表
    fun getMoreMessageList() {
        viewModelScope.launch {
            val list = _notificationBean.value!!.toMutableList()
            val resp = repo.getUserMessageList(msgId)
            resp.let {
                if (it.isNotEmpty()) {
                    msgId = it.last().id
                }
                list.addAll(it)
            }
            _notificationBean.value = list
        }
    }

    fun deleteMessage(iid: Long) {
        val list = _notificationBean.value?.toMutableList()
        list?.let {
            _notificationBean.value = list.filterNot { it.id == iid }
        }
    }

    fun selectMessage(type: Int) {
        val list = _notificationBean.value?.toMutableList()
        list?.let {
            if (type == 0) {
                _notificationSelect.value = list.ifEmpty { emptyList() }
            } else {
                _notificationSelect.value = list.filter { it.type == type }
            }
        }
    }
}