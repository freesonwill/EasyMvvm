package com.xcjh.app.websocket.listener

import com.xcjh.app.bean.BeingLiveBean
import com.xcjh.app.websocket.bean.FeedSystemNoticeBean
import com.xcjh.app.websocket.bean.LiveStatus
import com.xcjh.app.websocket.bean.PureFlowCloseBean
import com.xcjh.app.websocket.bean.ReceiveChangeMsg
import com.xcjh.app.websocket.bean.ReceiveChatMsg
import com.xcjh.app.websocket.bean.ReceiveWsBean
import com.xcjh.app.websocket.bean.UpdateLiveContentBean

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

    /**
     * 禁言 * 32 -》禁言  bizId=""如果是空就是全局    不为空就是主播id
     *
     */
    fun onProhibition(feed: FeedSystemNoticeBean){}


    /**
     * 解禁
     * 33-》解除
     */
    fun onOpeningUp(feed: FeedSystemNoticeBean){}

    /**
     * 是否黑名单
     */
    fun onIsBlacklist(feed: FeedSystemNoticeBean){}

    /**
     * 直播间信息修改
     */
    fun onUpdateLiveContent(content : UpdateLiveContentBean){}

}
/**
 * 直播间流状态
 */
interface LiveStatusListener {

    /**
     * 服务器主动推送直播间开播
     */
    fun onOpenLive(bean: LiveStatus){}
    /**
     * 服务器主动推送直播间关播
     */
    fun onCloseLive (bean:LiveStatus){}

    /**
     * 服务器主动推送直播间直播地址修改
     */
    fun onChangeLive (bean:LiveStatus){}
    /// 收到推送比分消息
    fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>){}



}

/**
 * 其他推送消息
 */
interface OtherPushListener {

    /**
     * 收到比赛实时推送数据
     */
    fun onChangeMatchData(matchList: ArrayList<ReceiveChangeMsg>){}

    /**
     * 首页主播开播推送
     */
    fun onAnchorStartLevel(beingLiveBean: BeingLiveBean){}

    /**
     * 新闻更新数据通知，新闻列表更新
     */
    fun onNewsUpdateDate(){}





}
/**
 * 未读消息总数
 */
interface NoReadMsgPushListener {

    /**
     * 收到比赛实时推送数据
     */
    fun onNoReadMsgNums(nums: String){}
    /**
     * 用户被踢下线
     */
    fun onUserIsKicked(){}
}
/**
 * 与主播单聊相关
 */
interface C2CListener {
    /// 发送消息是否成功
    fun onSendMsgIsOk(isOk: Boolean,bean:ReceiveWsBean<*>)
    /// 收到系统推送消息
    fun onSystemMsgReceive(chat: FeedSystemNoticeBean)
    /// 收到主播的消息
    fun onC2CReceive(chat: ReceiveChatMsg)
    // 收到推送消息 比分实时数据
    fun onChangeReceive(chat: ArrayList<ReceiveChangeMsg>)

    // 收到推送消息 比分实时数据
    fun onPureFlowClose(pure: PureFlowCloseBean){}
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

/**
 * 新建关播
 */
interface   MOffListener{
    /**
     * 服务器主动推送直播间关播
     */
    fun onCloseLive (bean:LiveStatus)
}