package com.xcjh.app.ui.search

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.request

class SearchVm : BaseViewModel() {
    //热门比赛标签
    var matchList = UnPeekLiveData<ArrayList<BeingLiveBean>>()
    //进行中的比赛
    var liveList = UnPeekLiveData<ArrayList<BeingLiveBean>>()
    var errTag= UnPeekLiveData<Boolean>()

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
}