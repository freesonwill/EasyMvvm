package com.xcjh.app.ui.chat

import com.haibin.calendarview.Calendar
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xcjh.app.R
import com.xcjh.app.appViewModel
import com.xcjh.app.bean.AutherInfoBean
import com.xcjh.app.bean.MsgBean
import com.xcjh.app.bean.PostClreaMsgBean
import com.xcjh.app.bean.HistoryMsgReq
import com.xcjh.app.net.apiService
import com.xcjh.app.ui.room.MsgBeanData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ApiResponse
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException


class ChatVm : BaseViewModel() {

    var feedbackOk = BooleanLiveData()
    var upPic = UnPeekLiveData<String>()
    var autherInfo = UnPeekLiveData<AutherInfoBean>()
    var clearMsg = UnPeekLiveData<Boolean>()
    var hisMsgList = UnPeekLiveData<MutableList<MsgBeanData>>()
    fun getHisMsgList(smartCommon: SmartRefreshLayout, offset: String, serchId: String?) {

        request(
            {
                apiService.getHistoryMsgPr(
                    HistoryMsgReq("2", null, offset, CacheUtil.getUser()?.id!!, serchId!!)
                )
            },

            {
                smartCommon.finishRefresh()
                smartCommon.resetNoMoreData()
                //if (it.size > 0) {
                hisMsgList.value = it
                // }
            }, {
                try {
                    smartCommon.finishRefresh()
                    smartCommon.resetNoMoreData()
                    //请求失败

                    hisMsgList.value = arrayListOf()
                    myToast(it.errorMsg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, false
        )
    }

    fun upLoadPic(file: File) {
        var fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
        var part = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
        request(
            {
                apiService.upLoadChatPic(part)
            },
            {

                upPic.value = it

            }, {
                //请求失败
                upPic.value = ""
                myToast(appContext.getString(R.string.http_txt_err_meg))

            }
        )
    }

    fun upLoadPic(file: MultipartBody.Part) {

        request(
            {
                apiService.upLoadChatPic(file)
            },
            {

                upPic.value = it

            }, {
                //请求失败
                upPic.value = ""
                myToast(appContext.getString(R.string.http_txt_err_meg))

            }
        )
    }

    suspend fun upLoadPicSuspend(file: MultipartBody.Part, block: (data: String) -> Unit) {
        //同时异步请求2个接口，请求完成后合并数据
        //  var data = ""
        withContext(Dispatchers.IO) {

            try {
                val data = async { apiService.upLoadChatPic(file) }
                block.invoke(data.await().data)
            } catch (e: IOException) {
                block.invoke("FAILE")
            }


        }

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
     * 获取主播信息
     */
    fun getUserInfo(id: String) {
        request(
            { apiService.getAutherInfo(id) },
            {

                autherInfo.value = it
            }, {

            }
        )
    }

}