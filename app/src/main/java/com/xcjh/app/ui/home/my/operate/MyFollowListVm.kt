package com.xcjh.app.ui.home.my.operate

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.EventsBean
import com.xcjh.app.bean.FollowAnchorBean
import com.xcjh.app.bean.ListReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class MyFollowListVm : BaseViewModel() {
    //进行中的比赛
    var followList = UnPeekLiveData<ListDataUiState<FollowAnchorBean>>()
    //取消关注
    var unfollow=UnPeekLiveData<Int>()
    //关注主播
    var followValue=UnPeekLiveData<Int>()

    private var pageNo = 1


    /**
     * 获取我关注的主播列表
     */
    fun getAnchorPageList(isRefresh: Boolean){
        if (isRefresh) {
            pageNo = 1
        }
        request(
            { apiService.getAnchorPageList(ListReq(current=pageNo)) },
            {
                pageNo++
                it.records.forEach {
                    it.isFollow=true
                }
                followList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                followList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
            }
        )
    }


    /**
     * 取消主播关注
     */
    fun unfollowAnchor(id:String,delete:Int){
        request(
            { apiService.unfollowAnchor(id) },
            {
                unfollow.value=delete
            }, {
                //请求失败
                myToast(appContext.getString(R.string.http_txt_err_meg))

            },isShowDialog=true
        )
    }

    /**
     * 关注主播
     */
    fun followAnchor(id: String,position:Int) {
        request({
            apiService.getNoticeUser(id)
        }, {
            followValue.value=position
        }, {
            myToast(it.errorMsg)
        },isShowDialog=true)
    }

}