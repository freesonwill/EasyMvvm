package com.xcjh.app.ui.home.my.personal

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.InfoReq
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.utils.myToast
import com.xcjh.base_lib.utils.request

class EditProfileVm : BaseViewModel() {
    var update= UnPeekLiveData<Boolean>()

    /**
     * 设置用户信息
     */
    fun setUserInfo(name:String?,head:String?) {
        request(
            { apiService.updateInfo(InfoReq(name = name, head = head)) },
            {
                update.value=true

            }, {
                myToast(it.errorMsg)
            },isShowDialog=true
        )
    }
}