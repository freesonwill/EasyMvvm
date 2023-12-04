package com.xcjh.app.ui.home.home.tab

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.LiveReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.request

class CompetitionTypeListVm  : BaseViewModel() {
    //进行中的比赛
    var liveList = UnPeekLiveData<ListDataUiState<BeingLiveBean>>()
    private var pageNo = 1
    //正在直播详情
    var beingLive=UnPeekLiveData<BeingLiveBean>()
    /**
     * 获取首页热门比赛
     */
    fun getNowLive(isRefresh: Boolean,type:String){
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getNowLive(LiveReq(current=pageNo, matchType = type)) },
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

    /**
     * 获取首页正在直播的详情刷新页面
     */
    fun getOngoingMatchList(id: String){

        request(
            { apiService.getLiveInfo(id) },
            {
                beingLive.value=it
            }, {
                //请求失败
                it.errorMsg.toString().loge("====")
//                hotList.value = arrayListOf()
            }
        )

    }
}