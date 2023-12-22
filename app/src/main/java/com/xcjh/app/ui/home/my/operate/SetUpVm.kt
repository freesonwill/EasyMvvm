package com.xcjh.app.ui.home.my.operate

import android.annotation.SuppressLint
import com.engagelab.privates.core.api.MTCorePrivatesApi
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class SetUpVm : BaseViewModel() {
    var exitLive= UnPeekLiveData<Boolean>()

    /**
     * 退出登录
     */
    fun exitLogin(){
        request(
            { apiService.exitLogin() },
            {
                exitLive.value=true

            }, {
                exitLive.value=true

                //请求失败
                myToast(it.errorMsg)

            },isShowDialog=true
        )
    }

    /**
     * 解绑推送
     */

    @SuppressLint("SuspiciousIndentation")
    fun unbindPush(){
      var regId=  MTCorePrivatesApi.getRegistrationId(appContext)
        request(
            { apiService.unbindPush(regId) },
            {
                exitLogin()
            }, {
                //请求失败
//                myToast(it.errorMsg)
                exitLogin()

            },isShowDialog=true
        )
    }


}