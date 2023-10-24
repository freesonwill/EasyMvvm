package com.xcjh.app.ui.chat

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.PostClreaMsgBean
import com.xcjh.app.bean.HistoryMsgReq
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class ChatVm : BaseViewModel() {

    var feedbackOk = BooleanLiveData()
    var upPic = UnPeekLiveData<String>()
    var clearMsg = UnPeekLiveData<Boolean>()
    var hisMsgList = UnPeekLiveData<MutableList<MsgBean>>()
    fun getHisMsgList(smartCommon:SmartRefreshLayout, offset: String, serchId:String?) {

        request(
            {
                apiService.getHistoryMsg(
                    HistoryMsgReq("2",null,offset, CacheUtil.getUser()?.id!!, serchId!!)
                )
            },

            {
                smartCommon.finishRefresh()
                smartCommon.resetNoMoreData()
                if (it.size>0) {
                    hisMsgList.value = it
                }
            }, {
                try {
                    smartCommon.finishRefresh()
                    smartCommon.resetNoMoreData()
                    //请求失败

                    myToast(it.errorMsg)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }, false
        )
    }
    fun upLoadPic(file: File) {
        var fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
        var part = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
        request(
            { apiService.upLoadChatPic(part) },
            {

                upPic.value = it

            }, {
                //请求失败
                upPic.value = ""
                myToast(appContext.getString(R.string.http_txt_err_meg))

            }
        )
    }
    /**
     * 反馈
     */
    fun clearMsg(id: String) {
        request(
            { apiService.getClreaMsg(PostClreaMsgBean(id)) },
            {
                clearMsg.value = true
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, false
        )
    }

    /**
     * 获取用户信息
     */
    fun getUserInfo() {
        request(
            { apiService.getUserBaseInfo() },
            {

                CacheUtil.setUser(it)
                appViewModel.userInfo.value=it
            }, {

            }
        )
    }

}