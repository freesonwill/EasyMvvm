package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.TopUpRecordsRepository
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.DateFilterBean
import com.walisport.module.topup.data.entity.DateFilterEnum
import com.walisport.module.topup.data.entity.RechargeFilterBean
import com.walisport.module.topup.data.entity.RechargeRecordBean
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import plugin.koin.KoinViewModel

@KoinViewModel
class TopUpRecordsViewModel(private val repository: TopUpRecordsRepository) : BaseViewModel() {

    private var page = 1
    private var isPageEnd = false

    private val _recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = UnPeekLiveData()
    val recordListChange: UnPeekLiveData<List<RechargeRecordBean>?> = _recordListChange

    private val _onDateFilter = MutableLiveData<DateFilterBean>()
    val onDateFilter: LiveData<DateFilterBean> get() = _onDateFilter

    private val _onRechargeFilter = MutableLiveData<List<RechargeFilterBean>>()
    val onRechargeFilter: LiveData<List<RechargeFilterBean>> get() = _onRechargeFilter

    init {
        _onDateFilter.value = DateFilterBean(
            title = DateFilterEnum.ALL.title,
            date = DateFilterEnum.ALL
        )
        _onRechargeFilter.value = listOf(RechargeFilterBean.getAllTypeBean())
    }

    fun reload() {
        changePageEnd(false)
        page = 1
        clearCurrentList()
        setState(LoadingState.Refreshing)
        getListData()
    }

    fun loadNextPage() {
        if (isPageEnd) {
            return
        }
        if (apiStateListener.value != LoadingState.LoadSuccess) {
            return
        }
        page++
        setState(LoadingState.LoadingNext)
        getListData()
    }

    fun changePageEnd(b: Boolean) {
        isPageEnd = b
    }

    private fun clearCurrentList() {
        repository.clearCurrentList()
    }

    fun getListData() {
        viewModelScope.launch {
            callApi({
                repository.getListData(page)
            }, {
                if (it is ApiResponseState.Failed) {
                    recordListChange.value = arrayListOf()
                } else if (it is ApiResponseState.Succeeded<*>) {
                    val list = it.dataAs<List<RechargeRecordBean>>()
                    val size = list?.size ?: 0
                    if (page == 1 && size == 0) {
                        recordListChange.value = arrayListOf()
                        setState(LoadingState.DataEmpty)
                    } else if (size < TopUpRecordsRepository.DEFAULT_LIST_SIZE) {
                        setState(DataState.NoMoreData)
                        recordListChange.value = list
                    } else {
                        setState(LoadingState.LoadSuccess)
                        recordListChange.value = list
                    }

                }
            })
        }
    }

    private fun mergeList(
        listA: List<RechargeRecordBean>?,
        listB: List<RechargeRecordBean>?
    ): List<RechargeRecordBean>? {
        if (listA == null) {
            return listB
        }

        if (listB == null) {
            return listA
        }

        val list = listA.toMutableList()
        list.addAll(listB)

        return list.toImmutableList()
    }
}