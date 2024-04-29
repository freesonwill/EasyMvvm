package com.cn.game.sdk.event


import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.event.EventLiveData

/**
 * 描述　:APP全局的ViewModel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppGameViewModel : BaseViewModel() {

    //socket状态消息
    var wsStatusGameClose = EventLiveData<Boolean>()//关闭
    var wsStatusGameOpen = EventLiveData<Boolean>()//开启

    var ceshEvent=EventLiveData<Boolean>()


    init {
        //默认值保存的账户信息，没有登陆过则为null started 或 resumed
//        this.updateLoginEvent.value = CacheUtil.isLogin()
//        this.userInfo.value = CacheUtil.getUser()

    }
}