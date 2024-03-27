package com.xcjh.app.ui.search

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class SearchVm : BaseViewModel() {
    //热门比赛标签
    var matchList = UnPeekLiveData<ArrayList<BeingLiveBean>>()
    //进行中的比赛
    var liveList = UnPeekLiveData<ArrayList<BeingLiveBean>>()
    var errTag= UnPeekLiveData<Boolean>()
    var hotMatchList = UnPeekLiveData<ListDataUiState<MatchBean>>()
    /**
     * 获取标签
     */
    fun getHotOngoingMatch() {
        var hot=HotReq()
        hot.top=5
        request(
            { apiService.getHotOngoingMatch(hot) },

            {
                matchList.value=it
            }, {
                //请求失败
                matchList.value=arrayListOf()
            }, true
        )
    }


    /**
     * 获取首页热门比赛
     */
    fun getNowLive(name:String){

        request(
            { apiService.getNowLive(LiveReq(current=1, name=name,size=50)) },
            {

                liveList.value =  it.records
            }, {
                //请求失败
                //请求失败
                liveList.value=arrayListOf()
            },isShowDialog=false
        )
    }
    private var pageNo = 1
    /**
     * 搜索赛事
     */
    fun getGameList(name:String,isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }else{
            pageNo++
        }
       var bean=PostSchMatchListNewBean(current=pageNo,size=20,name=name,status="3")
        request(
            { apiService.getHotMatchChildListSearch(bean) },

            {

                hotMatchList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it!!.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                hotMatchList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = true,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
                myToast(it.errorMsg)

            }
        )
    }
}