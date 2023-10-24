package com.xcjh.app.ui.home.my.personal

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.InfoReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PersonalDataVm : BaseViewModel() {
    var update= UnPeekLiveData<String>()




    /**
     * 上传头像
     */
    fun upLoadPic(file: File) {
        var fileRequestBody = RequestBody.create(MediaType.parse("image/jpeg"), file)
        var part = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)
        request(
            { apiService.upLoadPic(part) },
            {
                if(it.isNotEmpty()){
                    setUserInfo(name = null, head = it)
                }else{
                    myToast(appContext.getString(R.string.http_txt_err_meg))
                }

            }, {
                //请求失败

                myToast(appContext.getString(R.string.http_txt_err_meg))

            },isShowDialog = true
        )
    }

    /**
     * 设置用户信息
     */
    fun setUserInfo(name:String?,head:String?) {

        request(
            { apiService.updateInfo(InfoReq(name = name, head = head)) },
            {
                update.value = head!!

            }, {
                myToast(it.errorMsg)
            },isShowDialog=true
        )
    }

}