package com.xcjh.app.ui.login

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.LoginInfo
import com.xcjh.app.bean.PostLoaginBean
import com.xcjh.app.net.apiService
import com.xcjh.app.utils.CacheUtil
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request


class LoginVm : BaseViewModel() {

    var logain = UnPeekLiveData<String>()
    var codeData = UnPeekLiveData<String>()
    /**
     * 登录
     */
    fun getLogin(bean:PostLoaginBean) {
        request(
            { apiService.getLogin(bean) },

            {
                CacheUtil.setIsLogin(true, LoginInfo("","", it))
                logain.value=it
            }, {
                //请求失败
                myToast(it.errorMsg)
            }, true
        )
    }

}