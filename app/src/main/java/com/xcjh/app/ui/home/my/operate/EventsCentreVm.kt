package com.xcjh.app.ui.home.my.operate

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.EventsBean
import com.xcjh.app.bean.ListReq
import com.xcjh.app.bean.NewsBean
import com.xcjh.app.bean.NewsReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.request

class EventsCentreVm  : BaseViewModel() {
    //进行中的比赛
    var eventsList = UnPeekLiveData<ListDataUiState<EventsBean>>()

    private var pageNo = 1




    /**
     * 获取个人中心活动中心列表
     */
    fun getEventsList(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getEventsList(ListReq(status="1", current = pageNo)) },
            {
                pageNo++
                eventsList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                //请求失败
                eventsList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
            }
        )
    }

}