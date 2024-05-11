package com.xcjh.app.ui.chat

import android.util.Log
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.xcjh.app.R
import com.xcjh.app.bean.AutherInfoBean
import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.bean.PostClreaMsgBean
import com.xcjh.app.bean.HistoryMsgReq
import com.xcjh.app.net.apiService
import com.xcjh.app.bean.MsgBeanData
import com.xcjh.app.utils.CacheDataList
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.callback.livedata.BooleanLiveData
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException


class ChatVm : BaseViewModel() {



    var feedbackOk = BooleanLiveData()
    var upPic = UnPeekLiveData<String>()
    var autherInfo = UnPeekLiveData<AutherInfoBean>()
    var clearMsg = UnPeekLiveData<Boolean>()
    var emojiList=UnPeekLiveData<ArrayList<String>>()

    var offset = ""
    //获取到的列表数据
    var hisMsgList = UnPeekLiveData<MutableList<MsgBeanData>>()
    //获取数据是否正确默认是错误
    var isSuccess=false
    //默认是第一页
    var isRefresh=true
    fun getHisMsgList(smartCommon: SmartRefreshLayout, offsetId: String, serchId: String?) {
        isRefresh = offsetId.equals("")
        var   dd=HistoryMsgReq("2", null, offsetId,  CacheUtil.getUser()?.id!!, serchId!!, size = 100)
        request(
            {
                apiService.getHistoryMsgPr(
                    HistoryMsgReq("2", null, offsetId, userId=CacheUtil.getUser()?.id!!, searchId=serchId!!, size = 100)
                )
            },

            {
                smartCommon.finishRefresh()
                smartCommon.resetNoMoreData()
                isSuccess=true
                //if (it.size > 0) {


                var data=ArrayList<MsgBeanData>()



                if(it.size>0){
                    data.addAll(it)
                    if(offset.equals("")){
                       if(CacheUtil.getUser()!=null){
                           CacheDataList.globalCache[CacheUtil.getUser()!!.id+it[0].anchorId!!]=data
                           //获取是否有数据
                            var list=CacheUtil.getChatList()
                             var dataHistory=  list[CacheUtil.getUser()!!.id+it[0].anchorId!!]

                           if(dataHistory!=null){

                               val dataList = ArrayList<MsgBeanData>()
                               dataList.addAll(data)
                               for (item in dataHistory){

                                   if (item.sentNew==2) {
                                       dataList.add(item)
                                   }

                               }
                               dataList.sortBy { it.createTime }

                               data.clear()
                               data.addAll(dataList)

                               recordList(data,dataHistory)

                           }else{
                               //没有数据的时候就添加
                               list[CacheUtil.getUser()!!.id+it[0].anchorId!!]=data
                               CacheUtil.setChatList(list)


                           }


                       }

                    }
                    offset=it[0].id!!
                }
                hisMsgList.value = data

                // }
            }, {
                try {
                    isSuccess=false
                    smartCommon.finishRefresh()
                    smartCommon.resetNoMoreData()
                    //请求失败

                    hisMsgList.value = arrayListOf()
//                    myToast(it.errorMsg)
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


    fun getAllKeysFromJson( )  {

        val resourceId =  R.raw.emoji // 替换成你的 JSON 文件名
        val jsonString = appContext.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val keysList = ArrayList<String>()
        jsonObject.keys().forEach { key ->
            keysList.add(key)
        }

       emojiList.value=keysList

    }

    /**
     * 合并数组   第一个是当前获取到到数据   第二个是历史数据
     */
    fun  recordList(list : ArrayList<MsgBeanData>,newList : ArrayList<MsgBeanData>){

        val dataList = ArrayList<MsgBeanData>()
        dataList.addAll(list)
        for (item in newList){
//            val existingItem = dataList.find { (it.fromId+it.createTime) == (item.fromId+item.createTime)}
//            if (existingItem == null) {
//                dataList.add(item)
//            }
                if (item.sentNew==2) {
                    dataList.add(item)
                }



        }
        dataList.sortBy { it.createTime }
        //获取是否有数据
        var listDate=CacheUtil.getChatList()
        listDate[CacheUtil.getUser()!!.id+list[0].anchorId!!]=dataList
        CacheUtil.setChatList(listDate)
    }

}