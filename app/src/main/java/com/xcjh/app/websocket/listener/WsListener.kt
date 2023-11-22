package com.xcjh.app.websocket.listener

import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean

/**
 * @author zobo101
 * webSocket 返回的各种数据
 */


/**
 * 登录登出监听
 */
interface LoginOrOutListener {

    //
    fun onLoginIn(isOk: Boolean, msg: ReceiveWsBean<*>)

    fun onLoginOut(isOk: Boolean, msg: ReceiveWsBean<*>)
}

/**
 * 直播间群聊相关
 */
interface LiveRoomListener {

    /**
     * 进入房间成功
     */
    fun onEnterRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>)

    /**
     * 退出房间成功
     */
    fun onExitRoomInfo(isOk: Boolean, msg: ReceiveWsBean<*>)

    /**
     * 聊天信息
     */
    fun onRoomReceive(chat: ReceiveChatMsg)

    /// 发送消息是否成功
    fun onSendMsgIsOk(isOk: Boolean,bean:ReceiveWsBean<*>)

}
/**
 * 直播间流状态
 */
interface LiveStatusListener {

    /**
     * 服务器主动推送直播间开播
     */
    fun onOpenLive(bean: LiveStatus)
    /**
     * 服务器主动推送直播间关播
     */
    fun onCloseLive (bean:LiveStatus)

    /**
     * 服务器主动推送直播间直播地址修改
     */
    fun onChangeLive (bean:LiveStatus)

}

/**
 * 与主播单聊相关
 */
interface C2CListener {
    /// 发送消息是否成功
    fun onSendMsgIsOk(isOk: Boolean,bean:ReceiveWsBean<*>)

    /// 收到主播的消息
    fun onC2CReceive(chat: ReceiveChatMsg)
    /// 收到推送消息
    fun onChangeReceive(chat: MutableList<ReceiveChangeMsg>)
}


/**
 * 消息已读监听
 */
interface ReadListener {
    /// 消息已读是否发送成功
    fun onSendReadIsOk(isOk: Boolean,bean:ReceiveWsBean<*>)

    /// 收到已读消息
    fun onReadReceive(bean:ReceiveWsBean<*>)

}