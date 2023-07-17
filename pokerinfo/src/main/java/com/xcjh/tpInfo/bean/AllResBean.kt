package com.xcjh.tpInfo.bean

import androidx.annotation.Keep
import com.chad.library.adapter.base.entity.MultiItemEntity
import java.io.Serializable

/**
 * 所有去壳 返回的 实体类 =======================================================================
 */
@Keep
data class MyPages<T>(
    val current: String = "",
    val `data`: ArrayList<T> = arrayListOf(),
    val empty: Boolean = false,
    val pages: String = "",
    val size: String = "",
    val total: String = ""
)


/**
 * 登录信息
 */
@Keep
data class LoginInfo(
    var id: String = "",//	用户ID
    var tokenName: String = "",//token名称
    var tokenValue: String? = null,//token值
    // var user: UserInfo? = null,//用户信息
) : Serializable


/**
 * 用户信息
 */
@Keep
data class UserInfo(
    var id: String? = "",//	用户ID
    var picture: String? = "",//头像


    val apple: String = "",
    val appleNickname: String = "",
    var alipay: String = "",//支付宝
    var appId: String = "",//应用ID
    var areaCode: String = "",//区号
    var email: String = "",//邮箱
    var google: String = "",//谷歌
    var nickname: String = "",//用户名称
    var password: String = "",//名字
    var phone: String = "",//名字
    var portrait: String = "",//用户头像
    var userId: String = "",//用户ID
    var username: String = "",//用户名称
    var wechat: String = "",//微信
    var facebook: String = "",
    var facebookName: String = "",//facebook昵称
    var googleNickname: String = "",//google昵称

) : Serializable

