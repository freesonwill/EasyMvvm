package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.getFormatDate
import com.walisport.module.topup.R
import com.walisport.module.topup.data.DateFilterBean
import com.walisport.module.topup.data.DateFilterEnum
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.data.TopUpRecordsRepository
import com.walisport.module.topup.data.WithdrawFilterBean
import com.walisport.module.topup.data.WithdrawShowTypeEnum
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.RechargeRecordBean
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

class WithdrawRecordsViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    private val _onWithdrawFilter = MutableLiveData<List<WithdrawFilterBean>>()
    val onWithdrawFilter: LiveData<List<WithdrawFilterBean>> get() = _onWithdrawFilter

    private val _onShowTypeListener = MutableLiveData<Event<WithdrawShowTypeEnum>>()
    val onShowTypeListener: LiveData<Event<WithdrawShowTypeEnum>> get() = _onShowTypeListener

    private val _recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = UnPeekLiveData()
    val recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = _recordListChange

    private var page = 1
    var customTime: Long? = null
        private set

    init {
        _onDateFilter.value = DateFilterBean(
            title = DateFilterEnum.ALL.title,
            date = DateFilterEnum.ALL
        )
        _onWithdrawFilter.value = listOf(WithdrawFilterBean.getAllTypeBean())
    }

    fun reload() {
        page = 1
        getListData()
    }

    fun loadNextPage() {
        if (apiStateListener.value != LoadingState.LoadSuccess) {
            return
        }
        page++
        setState(LoadingState.LoadingNext)
        getListData()
    }

    private fun getListData() {
        viewModelScope.launch {
            callApi({
                repo.getListData(page)
            }, {
                if (it is ApiResponseState.Failed) {
                    _recordListChange.value = arrayListOf()
                } else if (it is ApiResponseState.Succeeded<*>) {
                    val list = it.dataAs<List<RechargeRecordBean>>()
                    val size = list?.size ?: 0
                    if (page == 1 && size == 0) {
                        _recordListChange.value = arrayListOf()
                        setState(LoadingState.DataEmpty)
                    } else {
                        setState(LoadingState.LoadSuccess)
                        _recordListChange.value = list
                    }
                }
            })
        }
    }

    fun setShowType(type: WithdrawShowTypeEnum) {
        val current = _onShowTypeListener.value?.peekContent()
        if (current == type && current != WithdrawShowTypeEnum.NONE) {
            _onShowTypeListener.value = Event(WithdrawShowTypeEnum.NONE)
        } else {
            _onShowTypeListener.value = Event(type)
        }
    }

    fun setDateFilter(dateFilter: DateFilterEnum) {
        _onDateFilter.value = DateFilterBean(
            title = dateFilter.title,
            date = dateFilter
        )
    }

    fun setWithdrawFilter(ids: List<Int>) {
        if (ids.size == 1 && ids.first() == WithdrawFilterBean.ALL_TYPE_ID) {
            _onWithdrawFilter.value = listOf(WithdrawFilterBean.getAllTypeBean())
        } else {
            viewModelScope.launch {
                _onWithdrawFilter.value = listOf(WithdrawFilterBean.getAllTypeBean())//repo.getSportByIds(ids)
            }
        }
    }

    fun setDateFilter(millisecond: Long) {
        customTime = millisecond
        val date = millisecond.getFormatDate()
        val title = R.string.date_picker_date_before.getString(date)
        _onDateFilter.value = DateFilterBean(
            title = title,
            date = DateFilterEnum.CUSTOM
        )
    }
}