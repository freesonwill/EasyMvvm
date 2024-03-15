package com.xcjh.app.event

import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.event.EventLiveData

/**
 *
 * 描述　:APP全局的ViewModel，可以在这里发送全局通知替代EventBus，LiveDataBus等未使用
 */
class EventViewModel : BaseViewModel() {
    //更新登录 登出
    val updateLoginEvent = EventLiveData<Boolean>()

    //全局ws消息
    val socketEvent = EventLiveData<Any>()

    //子任务数据
    val taskEvent = EventLiveData<String>()


}