package com.walisport.module.message.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.database.dao.MessageDao
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.data.NotificationBean
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class MessageMainViewModel(private val repo: MessageMainRepository) : BaseViewModel() {

    private val _notificationBean = MutableLiveData<List<NotificationBean>>()
    val notificationBean: LiveData<List<NotificationBean>> = _notificationBean

    private var cursorId: Long = 0L
    private var cursorType: Int = 0

    //全部未读消息
    private val _allUnreadMsg = MutableLiveData<Int>()
    val allUnreadMsg: LiveData<Int> = _allUnreadMsg

    private val _sysUnreadMsg = MutableLiveData<Int>()
    val sysUnreadMsg: LiveData<Int> = _sysUnreadMsg

    private val _actUnreadMsg = MutableLiveData<Int>()
    val actUnreadMsg: LiveData<Int> = _actUnreadMsg

    private val _matUnreadMsg = MutableLiveData<Int>()
    val matUnreadMsg: LiveData<Int> = _matUnreadMsg

    private val _payUnreadMsg = MutableLiveData<Int>()
    val payUnreadMsg: LiveData<Int> = _payUnreadMsg

    companion object {


        const val MSG_SYS = 1
        const val MSG_ACT = 2
        const val MSG_MAT = 3
        const val MSG_PAY = 4
    }

    init {
        viewModelScope.launch {
            repo.observeMessageBean().collect { data ->
                var allUnreadNum = 0
                var sysUnreadNum = 0
                var actUnreadNum = 0
                var matUnreadNum = 0
                var payUnreadNum = 0
                val temp = data.mapIndexed { _, item ->
                    val status = item.status
                    val type = item.type
                    if (status == 0) {
                        allUnreadNum += 1
                        when (type) {
                            MSG_SYS -> {
                                sysUnreadNum += 1
                            }

                            MSG_ACT -> {
                                actUnreadNum += 1
                            }

                            MSG_MAT -> {
                                matUnreadNum += 1
                            }

                            MSG_PAY -> {
                                payUnreadNum += 1
                            }
                        }
                    }
                    NotificationBean(
                        id = item.id,
                        type = type,
                        state = status,
                        title = item.title,
                        content = item.content,
                        createTime = item.time
                    )
                }
                _allUnreadMsg.postValue(allUnreadNum)
                _sysUnreadMsg.postValue(sysUnreadNum)
                _actUnreadMsg.postValue(actUnreadNum)
                _matUnreadMsg.postValue(matUnreadNum)
                _payUnreadMsg.postValue(payUnreadNum)
                _notificationBean.postValue(temp)
            }
        }
    }

    //删除指定消息
    fun deleteMessage(id: Long) {
        repo.updateMessageStatus(id, MessageDao.STATUS_DEL)
    }

    //将消息设为已读
    fun setMessageRead(id: Long) {
        repo.updateMessageStatus(id, MessageDao.STATUS_READ)
    }

    //获取系统消息列表
    fun getMessageList(type: Int) {
        cursorId = 0L
        cursorType = type
        callApi({
            repo.getMessageData(cursorId, cursorType)
        }, {
            if (it is ApiResponseState.Failed) {
                setState(DataState.NetworkUnavailable)
            } else if (it is ApiResponseState.Succeeded<*>) {
                val result = it.dataAs<List<NotificationBean>>()
                if (result != null) {
                    if (result.isNotEmpty()) {
                        cursorId = result.last().id
                    }
                }
                setState(DataState.LoadSuccess)
            }
        }, autoUpdateState = false)
    }

    //加载更多系统消息列表
    fun getMoreMessageList() {
        callApi({
            repo.getMessageData(cursorId, cursorType)
        }, {
            if (it is ApiResponseState.Failed) {
                setState(DataState.NetworkUnavailable)
            } else if (it is ApiResponseState.Succeeded<*>) {
                val result = it.dataAs<List<NotificationBean>>()
                if (result != null) {
                    if (result.isNotEmpty()) {
                        cursorId = result.last().id
                    }
                }
                setState(DataState.LoadSuccess)
            }
        }, autoUpdateState = false)
    }
}