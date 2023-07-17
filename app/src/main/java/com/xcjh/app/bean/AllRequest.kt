package com.xcjh.app.bean

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
data class Page(
    var current: Int? = 1,//当前页
    var size: Int? = 20,//每页条数
)


@Keep
data class PageReq(
    var pageNum: Int? = 1,//当前页
    var pageSize: Int? = 20,//每页条数
)



