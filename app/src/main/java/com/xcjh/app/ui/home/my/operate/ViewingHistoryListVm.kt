package com.xcjh.app.ui.home.my.operate

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.ListReq
import com.xcjh.app.bean.LiveReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.request

class ViewingHistoryListVm  : BaseViewModel() {
    //进行中的比赛
    var liveList = UnPeekLiveData<ListDataUiState<BeingLiveBean>>()
    private var pageNo = 1


    /**
     * 获取首页热门比赛
     */
    fun getHistoryLive(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getHistoryLive(ListReq(current=pageNo)) },
            {
                pageNo++
                liveList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                //请求失败
                liveList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
            }
        )
    }
}