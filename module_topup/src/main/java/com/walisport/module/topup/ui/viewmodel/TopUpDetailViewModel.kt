package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.data.model.UnPeekLiveData
import arch.cayenne.lib.base.data.remote.ApiResponseState
import arch.cayenne.lib.base.data.remote.ApiResponseState.Start.dataAs
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import com.walisport.module.topup.data.TopUpDetailRepository
import com.walisport.module.topup.data.constants.LoadingState
import com.walisport.module.topup.data.entity.RechargeDetailBean
import kotlinx.coroutines.launch
import plugin.koin.KoinViewModel

@KoinViewModel
class TopUpDetailViewModel(private val repo: TopUpDetailRepository) : BaseViewModel() {
    private var transactionId: String = ""

    private val _dataBean: UnPeekLiveData<RechargeDetailBean> = UnPeekLiveData()
    val dataBean: UnPeekLiveData<RechargeDetailBean> = _dataBean


    override fun initViewModel() {
        super.initViewModel()
    }

    fun setTransactionId(transactionId: String) {
        this.transactionId = transactionId
    }

    fun queryData() {

        viewModelScope.launch {
            "获取充值详情： ${this@TopUpDetailViewModel.transactionId}".logd(TAG)
            callApi({
                repo.queryData(this@TopUpDetailViewModel.transactionId)
            }, {
                if (it is ApiResponseState.Failed) {
                    setState(LoadingState.DataEmpty)
                } else if (it is ApiResponseState.Succeeded<*>) {
                    val bean = it.dataAs<RechargeDetailBean>()
                    _dataBean.value = bean!!
                    setState(LoadingState.LoadSuccess)
                }
            })
        }
    }


}