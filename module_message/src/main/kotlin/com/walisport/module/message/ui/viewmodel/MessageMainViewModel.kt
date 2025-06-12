package com.walisport.module.message.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.message.data.MessageMainRepository
import com.walisport.module.message.data.NotificationBean
import plugin.koin.KoinViewModel

@KoinViewModel
class MessageMainViewModel(private val repo: MessageMainRepository) : BaseViewModel() {

    private val _notificationBean = MutableLiveData<List<NotificationBean>>()
    val notificationBean: LiveData<List<NotificationBean>> = _notificationBean

    private val _notificationSelect = MutableLiveData<List<NotificationBean>>()
    val notificationSelect: LiveData<List<NotificationBean>> = _notificationSelect

    fun getMessageData() {
        val tmp = NotificationBean(
            1,
            1,
            "维护公告",
            "系统维护通知",
            "昨天 21:21",
            "系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时",
            "",
            "",
            "",
            ""
        )
        val tmp1 = NotificationBean(
            2,
            2,
            "活动通知",
            "系统维护通知",
            "昨天 21:21",
            "系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时",
            "",
            "",
            "",
            ""
        )
        val tmp2 = NotificationBean(
            3,
            3,
            "热门赛事",
            "国足对战日本，赢面大吗？",
            "昨天 21:21",
            "系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时",
            "https://static.fastbs55.com/data/6f201842163f7eaa60e15623957cdeaf.png",
            "",
            "",
            ""
        )
        val tmp3 = NotificationBean(
            4,
            4,
            "充值",
            "",
            "2024-8-11 15:23",
            "系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时，系统维护12小时",
            "",
            "¥5000.00",
            "EEPay",
            "支付成功"
        )
        val list = listOf(tmp, tmp1, tmp2, tmp3)
        _notificationBean.value = list
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