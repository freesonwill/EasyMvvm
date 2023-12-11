package com.xcjh.app.ui.home.my.operate

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.EventsBean
import com.xcjh.app.bean.ListReq
import com.xcjh.app.bean.PushBean
import com.xcjh.app.bean.PushErrBean
import com.xcjh.app.net.apiService
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.bean.ListDataUiState
import com.xcjh.base_lib.utils.request

class PushSetVm : BaseViewModel() {
    //推送的
    var pushBean = UnPeekLiveData<PushBean>()
    var switchValue= UnPeekLiveData<PushErrBean>()


    /**
     * 获取个人中心活动中心列表
     */
    fun getInfoPush( ){

        request(
            { apiService.getInfo() },
            {
                pushBean.value=it
            }, {
                //请求失败
                var push=PushBean()
                pushBean.value=push
            }
        )
    }

    /**
     * 设置推送
     * 推送通知设置切换[name参数 liveOpen:切换主播开播通知 followMatch:切换关注比赛通知; status参数 1开 0关
     */
    fun setPush(name :String,status:Boolean){
        var switch:Int=1
        if(status){
            switch=1
        }else{
            switch=0
        }

        request(
            { apiService.setSwitch(name,switch) },
            {

            }, {
                var pushErrBean=PushErrBean()
                pushErrBean.name=name
                pushErrBean.state=!status
                switchValue.value=pushErrBean
            }
        )
    }
}