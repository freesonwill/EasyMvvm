package com.xcjh.app.ui.home.my.operate

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.luck.picture.lib.entity.LocalMedia
import com.xcjh.app.R
import com.xcjh.app.bean.FeedbackReq
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.nice.Utils
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.LogUtils
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ContactUsVm : BaseViewModel() {
   var imageList=ArrayList<String>()
    var uploading= UnPeekLiveData<Boolean>()
    var submit=UnPeekLiveData<Boolean>()

    /**
     * 上传头像
     */
    fun upLoadPic(select:Int,list:ArrayList<LocalMedia>) {

        var num:Int=select
        var file:File?=null
        if(list[select].compressPath!=null){
            file=File(list[select].compressPath)
        }else{
              file=File(Utils.getRealPathFromURI(appContext, Uri.parse(list[select].path) ) )
        }

        var fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
        var part = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
        request(
            { apiService.upLoadImage(part) },
            {
                if(it.isNotEmpty()){

                    imageList.add(it)
                    if(num>=list.size-1){
                        uploading.value=true
                    }else{
                        num++
                        upLoadPic(num,list)
                    }

                }else{
                    myToast(appContext.getString(R.string.http_txt_err_meg))
                    uploading.value=true
                    loadingChange.dismissDialog
                }

            }, {
                //请求失败
                uploading.value=true
                myToast(appContext.getString(R.string.http_txt_err_meg))
                loadingChange.dismissDialog
            }
        )
    }


    /**
     * 提交反馈
     */
    fun submitFeedback(type:Int,content:String,imageList:ArrayList<String>){
        request(
            { apiService.submitFeedback(FeedbackReq(feedbackType = type, feedbackContent = content,feedbackImage=imageList)) },
            {
                submit.value=true
                myToast(appContext.getString(R.string.submit_success))
            }, {
                myToast(it.errorMsg)
            },isShowDialog=true
        )
    }


    fun showLoading(){
        loadingChange.showDialog.postValue("请求网络中...")
    }


}