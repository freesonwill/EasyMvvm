package com.xcjh.app.ui.home.home.tab

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.network.ExceptionHandle.handleException
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.*

class MainRecommendVm  : BaseViewModel() {
    //广告
    var bannerList = UnPeekLiveData<ArrayList<AdvertisementBanner>>()
    //热门比赛
    var hotList = UnPeekLiveData<ArrayList<MatchBean>>()
    //进行中的比赛
    var liveList = UnPeekLiveData<ListDataUiState<BeingLiveBean>>()

    private var pageNo = 1
    /**
     * 获取首页广告
     */
    fun getBannerList(){
        request(
            { apiService.getBannerList() },
            {
                bannerList.value=it
            }, {
                //请求失败
                it.errorMsg.toString().loge("====")
                bannerList.value = arrayListOf()
            }
        )
    }

    /**
     * 获取首页热门比赛
     */
    fun getOngoingMatchList(req: HotReq){
        request(
            { apiService.getOngoingMatch(req) },
            {
                hotList.value=it
            }, {
                //请求失败
                it.errorMsg.toString().loge("====")
                hotList.value = arrayListOf()
            }
        )

    }


    /**
     * 获取首页热门比赛
     */
    fun getNowLive(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }

        request(
            { apiService.getNowLive(LiveReq(current=pageNo)) },
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