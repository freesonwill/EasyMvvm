package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.viewmodel.Event
import arch.cayenne.lib.database.entity.RechargeRecordBean
import com.walisport.module.topup.data.TopUpRecordsRepository
import com.walisport.module.topup.data.constants.LoadingState
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import plugin.koin.KoinViewModel

@KoinViewModel
class TopUpRecordsViewModel(private val repository: TopUpRecordsRepository) : BaseViewModel() {

    private var page = 1
    private var isPageEnd = false

    private val _recordListChange: UnPeekLiveData<List<RechargeRecordBean>> = UnPeekLiveData()

    val recordListChange: UnPeekLiveData<List<RechargeRecordBean>> = _recordListChange


    override fun initViewModel() {
        super.initViewModel()

        viewModelScope.launch {
            repository.recordListChange.collect {
                _recordListChange.value = it
            }
        }
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
            "获取充值记录 $page".logd(TAG)
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
                        recordListChange.value = mergeList(recordListChange.value, list)
                    } else {
                        setState(LoadingState.LoadSuccess)
                        recordListChange.value = mergeList(recordListChange.value, list)
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