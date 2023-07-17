package com.xcjh.tpInfo.bean

import androidx.annotation.Keep
import com.xcjh.base_lib.Constants

/**
 * 所有请求实体类 =======================================================================
 */


//第一步登录请求
@Keep
data class LoginReq(
    var aggregateType: String? = "",//聚合登录类型 1->google 2->apple 3->facebook 4->phone 5->email 6->twitter 7->wallet
    var appId: String? = "",//	应用ID
    var areaCode: String? = null,
    var authId: String? = null,//聚合授权ID
    var data: String? = null,
    var email: String? = null,//邮箱
    var password: String? = null,
    var passwordType: String? = null,
    var phone: String? = null,
    var strSign: String? = null,
    var type: String? = "",//登录类型 1->手机验证码登录 2->邮箱验证码登录 3->密码登录 4->三方聚合登录 5->用户名登录
    var username: String? = null,
    var nickname: String? = null,
    var portrait: String? = null,
    var authName:String?=null
)
//充值成功
@Keep
data class PayBeanReq(
    var packageName: String? = "com.xcjh",
    var productId: String? = null,
    var purchaseToken: String? = null,
)
//充值订单
@Keep
data class PayReq(
    var id: String? = "",
    var payPlatform: Int? = 4,//支付类型:微信支付1,支付宝支付2,苹果支付3, Stripe Pay4
    var receipt: String? = "",//苹果支付加密数据
)
//第二步登录请求
@Keep
data class LoginReq2(
    var userId: String? = "",
    var userType: String? = null,
)
@Keep
data class EmailCodeReq(
    var email: String? = "",
    var type: Int = 1,
)
@Keep
data class EmailCheckReq(
    var code: String? = "",
    var email: String? = "",
    var type: Int = 1,//1->登录 2->绑定 3->修改密码
)
@Keep
data class UpdateUserReq(
    val appId: String = "8888",
    val nickname: String? = null,
    val password: String = "",
    val portrait: String? = null
)
@Keep
data class ResetPasswordReq(
    val areaCode: String? = null,
    val email: String? = null,
    val password: String = "",
    val phone: String? = null
)

@Keep
data class NewQuestionReq(
    var chatId: String? = "",//	会话ID
    var question: String? = "",//问题
)

@Keep
data class ShareLikeReq(
    var shareId: String = "",//分享会话ID
    var type: Int=1,//类型[0:取消点赞 1:点赞]
)
@Keep
data class LikeReq(
    var id: String = "",//市场ID
    var type: Int=1,//类型[0:取消点赞 1:点赞]
)

@Keep
data class ShareInterestReq(
    var shareId: String = "",//分享会话ID
)
@Keep
data class MarketInterestReq(
    var marketId: String = "",//市场ID
)
@Keep
data class CollectAnswerDTO(
    var answerIds: ArrayList<String> = arrayListOf(),//问题ID集合
)
@Keep
data class ShareDTO(
    var answerIds: ArrayList<String> = arrayListOf(),//问题ID集合
    var chatId: String = "",//	会话ID
)
@Keep
data class FeedbackDTO(
    var content:String = "",//问题
    var appId: String = Constants.APP_ID,//
)

@Keep
data class Page(
    var current: Int? = 1,//当前页
    var size: Int? = 20,//每页条数
)

//订阅请求
@Keep
data class UserSubscribeReq(
    val type: Int = 0,
    val userId: String = ""
)


@Keep
data class UserLoginBind(
    var aggregateType: String? = "",//聚合登录类型 1->google 2->apple 3->facebook 4->phone 5->email 6->twitter 7->wallet
    var areaCode: String? = null,//区号
    var authId: String? = null,//聚合授权ID
    var email: String? = null,//邮箱
    var phone: String? = null,
    var type: String? = "",//登录类型 1->手机验证码登录 2->邮箱验证码登录 3->密码登录 4->三方聚合登录 5->用户名登录
    var authName:String?=""
)

@Keep
data class PageReq(
    var pageNum: Int? = 1,//当前页
    var pageSize: Int? = 20,//每页条数
)

@Keep
data class RoomListReq(
    var pageNum: Int? = 1,//当前页
    var pageSize: Int? = 10,//每页条数
    var title:String?="",
    var country:String?="",
    var city:String?=""
)

/**
 * 分页请求
 */
@Keep
data class ListReq(
    val categoryId: String? = null,//类目ID
    val roomId: String? = null,
    val id: String? = null,//ID查询
    val isAsc: String? = null,
    val orderByColumn: String? = null,
    var pageNum: Int = 1,
    var pageSize: Int = 10,
    var status: Int = 2,
    val startTime: String? = null,
    val endTime: String? = null,
    var country:String?="",//国家
    val name: String? = null,//模糊搜索名称
)


