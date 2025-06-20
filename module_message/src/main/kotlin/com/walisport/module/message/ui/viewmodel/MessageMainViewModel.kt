package com.walisport.module.message.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.data.NotificationBean
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class MessageMainViewModel(private val repo: MessageMainRepository) : BaseViewModel() {

    private val _notificationBean = MutableLiveData<List<NotificationBean>>()
    val notificationBean: LiveData<List<NotificationBean>> = _notificationBean

    private val _notificationSelect = MutableLiveData<List<NotificationBean>>()
    val notificationSelect: LiveData<List<NotificationBean>> = _notificationSelect

    private var cursorId: Long = 0L
    private var cursorType: Int = 0

    companion object {
        const val TYPE_DEFAULT = 0
        const val STATUS_READ = 1
        const val STATUS_DEL = 2
    }

    init {
        viewModelScope.launch {
            repo.observeMessageBean().collect { data ->
                val temp = data.mapIndexed { _, item ->
                    NotificationBean(
                        id = item.id,
                        type = item.type,
                        state = item.status,
                        title = item.title,
                        content = item.content,
                        createTime = item.time
                    )
                }
                if (cursorType == TYPE_DEFAULT) {
                    _notificationBean.value = temp
                } else {
                    _notificationBean.value = temp.filter { it.type == cursorType }
                }
            }
        }
    }

    //删除指定消息
    fun deleteMessage(id: Long) {
        repo.updateMessageStatus(id, STATUS_DEL)
    }

    //将消息设为已读
    fun setMessageRead(id: Long) {
        repo.updateMessageStatus(id, STATUS_READ)
    }

    //获取系统消息列表
    fun getMessageList() {
        cursorId = 0L
        val result = repo.getMessageList(cursorId, cursorType)
        result.let {
            if (result.isNotEmpty()) {
                cursorId = result.last().id
            }
        }
    }

    //加载更多系统消息列表
    fun getMoreMessageList() {
        val result = repo.getMessageList(cursorId, cursorType)
        result.let {
            if (result.isNotEmpty()) {
                cursorId = result.last().id
            }
        }
    }

    fun selectMessage(type: Int) {
        cursorType = type
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