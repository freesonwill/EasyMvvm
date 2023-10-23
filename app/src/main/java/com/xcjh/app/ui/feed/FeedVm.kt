package com.xcjh.app.ui.feed

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.Constants.Companion.BASE_PAGE_SIZE
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class FeedVm : BaseViewModel() {
    private var pageNo = 1

    //进行中的比赛
    var feedList = UnPeekLiveData<ListDataUiState<FeedBackBean>>()

    /**
     * 获取标签
     */
    fun getFeedNoticeList(isRefresh: Boolean) {
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getFeedNoticeList(BasePage(pageNo,BASE_PAGE_SIZE)) },

            {
                pageNo
                feedList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it!!.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                feedList.value = ListDataUiState(
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