package com.xcjh.app.ui.home.my.operate

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.net.apiService
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
                exitLive.value=false

                //请求失败
                myToast(it.errorMsg)

            },isShowDialog=true
        )
    }
}