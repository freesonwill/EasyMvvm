package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.constants.DataState
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.database.entity.RechargeRecordBean
import com.walisport.module.topup.data.TopUpRecordsRepository
import com.walisport.module.topup.data.constants.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList
import plugin.koin.KoinViewModel

@KoinViewModel
class TopUpRecordsViewModel(private val repository: TopUpRecordsRepository) : BaseViewModel() {

    private var page = 1
    private var isPageEnd = false

    val recordListChange by lazy { MutableLiveData<List<RechargeRecordBean>>() }


    override fun initViewModel() {
        super.initViewModel()
    }

    fun reload() {
        changePageEnd(false)
        page = 1
        val preState = apiStateListener.value
        setState(LoadingState.Refreshing)
        viewModelScope.launch(Dispatchers.IO) {
            clearCurrentMatch()
            if (preState == LoadingState.DataEmpty || preState == DataState.NetworkUnavailable) {
                getListData()
            }
        }
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

    private fun clearCurrentMatch() {
        repository.clearCurrentMatch()
    }

    fun getListData() {
        viewModelScope.launch {
            "获取充值记录 $page".logd(TAG)
            callApi({
                repository.getCollectData(page)
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
                    }

                    recordListChange.value = mergeList(recordListChange.value, list)

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