package com.xcjh.app.websocket.bean

/**
 * 发出的数据
 *
 */
data class SendCommonWsBean(
    /**
     *  操作类型
     *
     *  5 登录 22 注销
     *  7 加入群聊 21 退出群聊
     *  13 心跳包
     *  19 获取指定群聊或好友历史或离线消息
     */
    val cmd: Int? = null,
    // 当前登录用户的id
    val userId: String? = null,
    // 当前用户的token
    val token: String? = null,
    // 当前用户登录的平台,直播端:2 , app端：3
    val loginType: String? = "3",


    // 当前直播间的群聊ID 群聊ID【群聊消息独有】
    val groupId: String? = null,
    //好友ID【私聊消息独有】
    val fromUserId: String? = null,
    //source（字符串）: 登陆来源 1IOS 2android 3H5 4小程序
    val source: String? = "1",

)

/**
 * 发送消息
 * 群聊和私聊通用
 * cmd:11
 */
data class SendChatMsgBean(
    val chatType: Int? = 1,   // 聊天类型，群聊：1， 私聊：2
    val msgType: Int? = 0, //消息类型，文字：0， 图片：1
    val cmd: Int? = 11,   // 11
    val from: String? = "",   // 发送者ID
    val anchorId: String? = null,   // 当前聊天主播的id【私聊独有，不管是发送还是接收】
    val to: String? = "",   // 接收者ID【私聊独有】主播端
    val content: String? = "",   // 消息内容
    val identityType: String? = "0",   // 发送者类型：0-APP普通用户，1-LIVE端主播，2-LIVE端助手，3-LIVE端运营
    val createTime: Long? = 0,   // 消息时间
    val sendId: String? = "0", //消息唯一性，用来判断回调
    val toAvatar: String? = null, //接收者头像（主播、运营、助手均显示主播头像）
    val toNickName: String? = null, //接收者昵称（主播、运营、助手均显示主播昵称）
    val fromAvatar: String? = "", //发送者头像（主播、运营、助手均显示主播头像）
    val fromNickName: String? = "0", //发送者昵称（主播、运营、助手均显示主播昵称）
    val level: String? = "0", //用户等级（发送者为用户时必填）
    val groupId: String? = null,   // 群聊ID【群聊独有】
)

/**
 * 消息已读回复（收到消息回复内容）【cmd:23】
 */
data class ReadWsBean(
    val cmd: Int? = 23,
    val messageId: String? = "",//messageId
    val read: Int? = 1,// 0-未读， 1-已读
    val currentId: String? = null,//当前用户ID
    val channelType: Int? = 3,//消息发送平台：2-LIVE, 3-APP
    val toId: String? = "3",//接收人ID
)