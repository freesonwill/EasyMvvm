package com.xcjh.app.ui.notice

import android.util.Log
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.BasePage
import com.xcjh.app.bean.HotMatchBean
import com.xcjh.app.bean.HotMatchReq
import com.xcjh.app.bean.MatchBean
import com.xcjh.app.bean.PostSchMatchListBean
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.Constants.Companion.BASE_PAGE_SIZE
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request


class MyNoticeVm : BaseViewModel() {
    private var pageNo = 1

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

                myToast(appContext.getString(R.string.collect_success))
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
     * 晒哼
     */
    fun getMyNoticeList(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getMyNoticeList(BasePage(pageNo,BASE_PAGE_SIZE)) },

            {
                pageNo++
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

            }, true
        )
    }
}