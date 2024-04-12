package com.xcjh.app.ui.details.common

import androidx.databinding.ObservableBoolean
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.HistoryMsgReq
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.bean.ProhibitionBean
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.databind.StringObservableField
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class RoomChatVm : BaseViewModel() {
    //获取到主播是否禁言或者踢出
     var anchorControl=UnPeekLiveData<ProhibitionBean>()

    var hisMsgList = UnPeekLiveData<ListDataUiState<MsgBean>>()
    fun getHisMsgList(groupId: String? = "", offset: String,isRefresh:Boolean=false) {
        request(
            {
                apiService.getHistoryMsg(
                    HistoryMsgReq("1",
                        groupId,
                        offset,
                        userId = CacheUtil.getUser()?.id)
                )
            },
            {
                hisMsgList.value = ListDataUiState(true, listData = it, isRefresh =isRefresh )
            }, {
                hisMsgList.value = ListDataUiState(false, errMessage = it.errorMsg,isRefresh =isRefresh )
            }, false
        )
    }

    /** 消息输入框内容 */
    var input = StringObservableField()
    /** 是否可以发送消息 */
     var isSendEnable = object : ObservableBoolean(input){
         override fun get(): Boolean {
             return true
         }
     }

    /** 当前输入的消息数据类 */
    fun getMessages(): List<MsgBean> {
        val messages = listOf(MsgBean( CacheUtil.getUser()?.id,CacheUtil.getUser()?.head,CacheUtil.getUser()?.name?:"me", CacheUtil.getUser()?.lvNum, content = input.get(),identityType=0))
        input.set("")
        return messages
    }


    /**
     * 主播对当前用户禁言和踢出直播间信息
     */
    fun getAnchorControlUserInfo(userId: String) {
        request(
            { apiService.getAnchorControlUserInfo(userId) },

            {

                anchorControl.value = it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }

}