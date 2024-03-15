package com.xcjh.app.ui.home.msg

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.*
import com.xcjh.app.net.apiService
import com.xcjh.app.bean.MsgListNewData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.Constants
import com.xcjh.base_lib.Constants.Companion.BASE_PAGE_SIZE
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class MsgVm : BaseViewModel() {
    private var pageNo = 1
    private var pageNoFriend = 1

    //获取到消息列表
    var msgList = UnPeekLiveData<ListDataUiState<MsgListNewData>>()
    var frendList = UnPeekLiveData<ListDataUiState<FriendListBean>>()
    var clreaMsg = UnPeekLiveData<Boolean>()
    var delMsg = UnPeekLiveData<Boolean>()
    var upUserInfo = UnPeekLiveData<Boolean>()
    var clreaAllMsg = UnPeekLiveData<Boolean>()
    var unNoticeFri = UnPeekLiveData<Boolean>()
    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        request(
            { apiService.getUserBaseInfo() },
            {

                CacheUtil.setUser(it)
                Constants.ISSTOP_TALK = it.ynForbidden!!
                appViewModel.userInfo.value = it
                upUserInfo.value=true
            }, {
                upUserInfo.value=false
            }
        )
    }
    fun getNoticeUser() {//测试用
        request({
            apiService.getNoticeUser("1694272771701133312")
        }, {

        }, {
            myToast(it.errorMsg)
        })
    }
    fun getUnNoticeFriend(id:String) {//清楚消息
        request({
            apiService.getUnNoticeFriend(id)
        }, {
            unNoticeFri.value=true
        }, {
            myToast(it.errorMsg)
        })
    }
    fun getDelMsg(id:String) {//清楚消息
        request({
            apiService.delMsgByid(DelMsgBean(id))
        }, {
            delMsg.value=true
        }, {
            myToast(it.errorMsg)
        })
    }
    fun getClreaMsg(id:Int) {//清楚消息
        request({
            apiService.getClreaMsg(PostClreaMsgBean(id.toString()))
        }, {
            clreaMsg.value=true
        }, {
            myToast(it.errorMsg)
        })
    }
    fun getClreaAllMsg() {//清楚消息
        request({
            apiService.getClreaAllMsg()
        }, {
            clreaAllMsg.value=true
        }, {
            myToast(it.errorMsg)
        })
    }

    /**
     * 获取消息列表
     */
    fun getMsgList(isRefresh: Boolean,userName:String?) {
        if (isRefresh) {
            pageNo = 1
        }
        request(
            {
                apiService.getMsgListPage(
                    PostGetMsgBean(
                        pageNo,
                        100, userName.toString()
                    )
                )
            },

            {
                pageNo++
                msgList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it!!.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                msgList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
             //   myToast(it.errorMsg)
            }, false
        )
    }

    fun getFriendList(isRefresh: Boolean,name:String?) {
        if (isRefresh) {
            pageNoFriend = 1
        }
        request(
            {
                apiService.getFriendsList(
                    PostGetMsgBean(
                        pageNoFriend,
                        BASE_PAGE_SIZE, name!!
                    )
                )
            },

            {
                pageNoFriend++
                frendList.value = ListDataUiState(
                    isSuccess = true,
                    isRefresh = isRefresh,
                    isEmpty = it!!.records.isEmpty(),
                    isFirstEmpty = isRefresh && it.records.isEmpty(),
                    listData = it.records
                )
            }, {
                //请求失败
                frendList.value = ListDataUiState(
                    isSuccess = false,
                    isRefresh = isRefresh,
                    errMessage = it.errorMsg,
                    listData = arrayListOf()
                )
                myToast(it.errorMsg)
            }, false
        )
    }
}