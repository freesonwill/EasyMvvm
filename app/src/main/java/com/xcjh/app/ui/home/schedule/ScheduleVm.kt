package com.xcjh.app.ui.home.schedule

import android.util.Log
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.HotMatchReq
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import org.json.JSONObject


class ScheduleVm : BaseViewModel() {
    private var pageNo = 1

    var hotMatch = UnPeekLiveData<ArrayList<HotMatchBean>>()
    var hotMatchList = UnPeekLiveData<ListDataUiState<MatchBean>>()
    var noticeData = UnPeekLiveData<Boolean>()
    var unnoticeData = UnPeekLiveData<Boolean>()
    /**
     * 关注
     */
    fun getNotice(matchId: String,matchType:String) {
        request(
            { apiService.getNoticeRaise(matchId,matchType) },

            {
                noticeData.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }
    /**
     * 取消关注
     */
    fun getUnnotice(matchId: String,matchType:String) {
        request(
            { apiService.getUnNoticeRaise(matchId,matchType) },

            {
                unnoticeData.value=true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }
    /**
     * 反馈
     */
    fun getHotMatchData(id: String,staus:String) {
        request(
            { apiService.getHotMatch(HotMatchReq(id, staus)) },

            {
                hotMatch.value=it
            }, {
                //请求失败

            }, false
        )
    }
    /**
     * 反馈
     */
    fun getHotMatchDataList(isRefresh: Boolean,isLoading: Boolean, bean: PostSchMatchListBean) {
        LogUtils.d("请求参数==="+com.alibaba.fastjson.JSONObject.toJSONString(bean))
        if (isRefresh) {
            pageNo = 1
        }
        Log.i("VVVVVVVV","====="+isLoading)
        request(
            { apiService.getHotMatchChildList(bean) },

            {
                pageNo
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
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
                myToast(it.errorMsg)

            }, isLoading
        )
    }
}