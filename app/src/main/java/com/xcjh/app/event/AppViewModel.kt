package com.xcjh.app.event

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.event.EventLiveData
import com.xcjh.app.bean.UserInfo
import com.xcjh.app.utils.CacheUtil

/**
 * 描述　:APP全局的ViewModel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppViewModel : BaseViewModel() {

    //更新用户登录登出 状态
    var updateLoginEvent = EventLiveData<Boolean>()

    var updateHistory = EventLiveData<Boolean>()//0 首页历史数据
    //更新用户信息
    var userInfo: UnPeekLiveData<UserInfo> = UnPeekLiveData.Builder<UserInfo>().setAllowNullValue(true).create()

    //App主题颜色 中大型项目不推荐以这种方式改变主题颜色，比较繁琐耦合，且容易有遗漏某些控件没有设置主题色
    var appColor = EventLiveData<Int>()

    init {
        //默认值保存的账户信息，没有登陆过则为null
        this.updateLoginEvent.value = CacheUtil.isLogin()
        this.userInfo.value = CacheUtil.getUser()

    }
}