package com.xcjh.app.web

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.EventsBean
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.request

class WebRichTextVm : BaseViewModel() {
    //获取详情
    var events = UnPeekLiveData<EventsBean>()


    /**
     * 聊天界面使用的所有列表随机问题
     */
    fun getActivityInfo(id:String){
        request(
            { apiService.getActivityInfo(id) },
            {
                events.value=it
            }, {
                //请求失败
                events.value=EventsBean()

            },isShowDialog=true
        )
    }
}