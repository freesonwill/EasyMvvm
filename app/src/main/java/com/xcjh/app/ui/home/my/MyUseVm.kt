package com.xcjh.app.ui.home.my

import android.util.Log
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.R
import com.xcjh.app.bean.AdvertisementBanner
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class MyUseVm : BaseViewModel() {
    var advertisement= UnPeekLiveData<AdvertisementBanner>()
    var advertisementErr= UnPeekLiveData<Boolean>()
    var exitLive= UnPeekLiveData<Boolean>()

    /**
     * 获取个人中心广告
     */
    fun getIndividualCenter(){
//        request(
//            { apiService.getIndividualCenter() },
//            {
//                advertisement.value=it
//            }, {
//
//                advertisementErr.value=false
//                //请求失败
////                myToast(appContext.getString(R.string.http_txt_err_meg))
//
//            }
//        )
    }


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