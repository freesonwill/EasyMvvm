package com.xcjh.app.ui.home.home.tab

import android.util.Log
import android.view.Gravity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.HotReq
import com.xcjh.app.bean.LiveReq
import com.xcjh.app.bean.MainDataBean
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.loge
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainRecommendNewVm : BaseViewModel() {
    //下拉刷新组和的三个接口
    var dateSetOf= MainDataBean()
    //热门比赛
    var hotList = UnPeekLiveData<ArrayList<MatchBean>>()
    //进行中的比赛
    var liveList = UnPeekLiveData<ListDataUiState<BeingLiveBean>>()
    private var pageNo = 1


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
    /**
     * 获取首页广告
     */
    fun getBannerList(){
        dateSetOf= MainDataBean()
        request(
            { apiService.getBannerList() },
            {
                dateSetOf.advertisement=it
                getOngoingMatchList(HotReq())
            }, {
                myToast(appContext.getString(R.string.main_err_title), gravity = Gravity.CENTER)
                //请求失败
//                getOngoingMatchList(HotReq())
                viewModelScope.launch {
//                    delay(10000) // delay for 10 seconds
                    delay(10000) // delay for 10 seconds
                    getBannerList()
                }

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
                dateSetOf.match=it
                getNowLive(true)
            }, {
                //请求失败
                getNowLive(true)
            }
        )

    }

    /**
     * 获取首页正在直播比赛
     */
    fun getNowLive(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }

        request(
            { apiService.getNowLive(LiveReq(current=pageNo,size=500)) },
            {
                pageNo++

                Log.i("CCeerereCc","it.records==="+it.records[0].status)
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
                    isFirstEmpty =true,
                    listData = arrayListOf()
                )
            }
        )
    }


    /**
     * 刷新轮休首页热门比赛
     */
    fun getRefreshMatchList(req: HotReq){
        request(
            { apiService.getOngoingMatch(req) },
            {
                hotList.value=it

            }, {
                //请求失败
                hotList.value=arrayListOf()
            }
        )

    }




}