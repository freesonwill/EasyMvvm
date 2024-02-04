package com.xcjh.app.event

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xcjh.app.bean.CurrentIndex
import com.xcjh.app.bean.UserInfo
import com.xcjh.app.bean.MsgBeanData
import com.xcjh.app.utils.CacheUtil
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.event.EventLiveData

/**
 * 描述　:APP全局的ViewModel，可以存放公共数据，当他数据改变时，所有监听他的地方都会收到回调,也可以做发送消息
 * 比如 全局可使用的 地理位置信息，账户信息,App的基本配置等等，
 */
class AppViewModel : BaseViewModel() {

    //更新用户登录登出 状态
    var updateLoginEvent = EventLiveData<Boolean>()
    //更新viewPager2是否可以滑动
    var updateViewpager = EventLiveData<Boolean>()
    //更新选择的日期
    var updateganlerTime = EventLiveData<String>()
    //更新首页消息数
    var updateMainMsgNum= EventLiveData<String>()
    var updateHistory = EventLiveData<Boolean>()//0 首页历史数据
    //更新用户信息
    var userInfo: UnPeekLiveData<UserInfo> = UnPeekLiveData.Builder<UserInfo>().setAllowNullValue(true).create()

    //App主题颜色 中大型项目不推荐以这种方式改变主题颜色，比较繁琐耦合，且容易有遗漏某些控件没有设置主题色
    var appColor = EventLiveData<Int>()
    //需要轮询的时候通知
    var appPolling=EventLiveData<Boolean>()

    //比分推送通知
    var appPushMsg=EventLiveData< ArrayList<ReceiveChangeMsg>>()
    //主播开播推送
    var appPushLive=EventLiveData<LiveStatus>()
    //点击消息TAB
    var appMsgResum=EventLiveData<Boolean>()
    //更新消息列表
    var updateMsgEvent = EventLiveData<String>()
    //更新消息列表数据
    var updateMsgListEvent = EventLiveData<MsgBeanData>()
    var updateSchedulePosition = EventLiveData<CurrentIndex>()

    var updateCollection = EventLiveData<Boolean>()
    //更首页显示ViewPager切换  -1是切换到首页并且是推荐页面
    var mainViewPagerEvent= EventLiveData<Int>()
    //切换home的ViewPager的切换  0就是推荐依次类推
    var homeViewPagerEvent= EventLiveData<Int>()
    //socket状态消息
    var wsStatusClose = EventLiveData<Boolean>()//关闭
    var wsStatusOpen = EventLiveData<Boolean>()//开启
    //更新某些页面
    var updateSomeData = EventLiveData<String>()//"friends"//好友列表
    var mainDateShowEvent=EventLiveData<Boolean>()
    init {
        //默认值保存的账户信息，没有登陆过则为null started 或 resumed
        this.updateLoginEvent.value = CacheUtil.isLogin()
        this.userInfo.value = CacheUtil.getUser()

    }
}