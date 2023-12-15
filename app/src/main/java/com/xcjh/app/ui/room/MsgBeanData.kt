package com.xcjh.app.ui.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_db")
class MsgBeanData {
    @ColumnInfo(name = "fromId")
    var fromId: String? = ""//发送用户ID
    @ColumnInfo(name = "avatar")
    var avatar: String? = ""//发送者头像
    @ColumnInfo(name = "nick")
    var nick: String = ""//发送者昵称
    @ColumnInfo(name = "level")
    var level: String? = "0"//级别
    @ColumnInfo(name = "content")
    var content: String = ""//消息内容
    @ColumnInfo(name = "chatType")
    var chatType: Int? = 0//聊天类型;(如1:公聊、2私聊)
    @ColumnInfo(name = "cmd")
    var cmd: Int? = 0
    @ColumnInfo(name = "anchorId")
    var anchorId: String? = ""//主播ID
    @ColumnInfo(name = "createTime")
    var createTime: Long? = 0
    @ColumnInfo(name = "creator")
    var creator: String? = ""
    @ColumnInfo(name = "delFlag")
    var delFlag: Int? = 0//APP用户是否删除 1删除 0正常
    @ColumnInfo(name = "groupId")
    var groupId: String? = ""//群组ID
    @ColumnInfo(name = "id")
    var id: String? = ""
    @ColumnInfo(name = "sendId")
    var sendId: String? = "0" //消息唯一性，用来判断回调
    @ColumnInfo(name = "identityType")
    var identityType: Int? = 0//发送者身份身份(0：普通用户，1主播 2助手 3运营)
    @ColumnInfo(name = "msgType")
    var msgType: Int? = 0 //消息类型，文字：0， 图片：1
    @ColumnInfo(name = "readable")
    var readable: Int? = 0//是否已读：0 未读 1 已读
    @ColumnInfo(name = "sent")
    var sent: Int? = 0//是否已发送: 0 正在发送 1 已发送 2 发送失败
    @ColumnInfo(name = "toId")
    var toId: String? = ""
    @ColumnInfo(name = "updateTime")
    var updateTime: String? = ""
    @ColumnInfo(name = "lastShowTimeStamp")
    var lastShowTimeStamp:  Long = 0
    @ColumnInfo(name = "updater")
    var updater: String? = ""

    @PrimaryKey(autoGenerate = true)
    var idd = 0

}