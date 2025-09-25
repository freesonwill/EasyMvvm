package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.DateFilterBean
import com.walisport.module.topup.data.entity.DateFilterEnum
import com.walisport.module.topup.data.entity.RechargeRecordBean
import com.walisport.module.topup.data.entity.WithdrawFilterBean
import kotlinx.coroutines.launch

class WithdrawRecordsViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    private val _recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = UnPeekLiveData()
    val recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = _recordListChange

    private var page = 1
    var customTime: Long? = null
        private set

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    private val _onWithdrawFilter = MutableLiveData<List<WithdrawFilterBean>>()
    val onWithdrawFilter: LiveData<List<WithdrawFilterBean>> get() = _onWithdrawFilter

    fun reload() {
        page = 1
        getListData()
    }

    init {
        _onDateFilter.value = DateFilterBean(
            title = DateFilterEnum.ALL.title,
            date = DateFilterEnum.ALL
        )
        _onWithdrawFilter.value = listOf(WithdrawFilterBean.getAllTypeBean())
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
}