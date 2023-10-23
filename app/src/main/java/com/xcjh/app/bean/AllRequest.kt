package com.xcjh.app.bean

import androidx.annotation.Keep
import com.xcjh.base_lib.Constants
import java.io.Serializable

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
    var authName: String? = null
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


/**
 * 首页热门比赛
 */
@Keep
data class HotReq(
    var matchType: String? = null,//比赛类型：1：足球；2：篮球,可用值:1,2
    var top: Int = 20

)

/**
 * 首页热门比赛
 */
@Keep
data class HotMatchReq(
    var matchType: String? = null,//比赛类型：1：足球；2：篮球,可用值:1,2
    var status: Int = 0

)
@Keep
data class PostClreaMsgBean(
    var anchorId: String

)
data class PostSchMatchListBean(
    val competitionId: String,//赛事ID
    val current: Int,
    val endTime: String,
    val matchType: String,//	比赛类型：1：足球；2：篮球,可用值:1,2
    val size: Int,
    val startTime: String,
    val status: Int//	查询已完成比赛因为篮球和足球状态不一致，这里统一使用99代表查询已完赛
)

/**
 * 首页正在直播的接口
 */
@Keep
data class BasePage(
    var current: Int = 1,//第几页
    var size: Int = Constants.BASE_PAGE_SIZE//每页显示多少条
)

@Keep
data class PostGetMsgBean(
    var current: Int = 0,//第几页
    var size: Int = Constants.BASE_PAGE_SIZE,//每页显示多少条
    var userName: String

)

data class PostLoaginBean(
    val account: String,
    val code: String?=null,
    val passwd: String? = null,
    val registerAddr: String? = null,
    val type: Int
)

/**
 * 首页正在直播的接口
 */
@Keep
data class LiveReq(
    var current: Int = 0,//第几页
    var size: Int = 20,//每页显示多少条
    var matchType: String? = null,//比赛类型 1足球，2篮球,可用值:1,2
    var name: String? = null,//名称 (主播名称、赛事名称、比赛)
    var exceptId: String? = null,//排除ID
    var matchId: String? = null,//比赛ID
)

/**
 * 首页新闻列表
 */
@Keep
data class NewsReq(
    var current: Int = 0,//第几页
    var size: Int = 20,//每页显示多少条

)

/**
 * 列表的课重复的请求
 */
@Keep
data class ListReq(
    var current: Int = 0,//第几页
    var size: Int = 20,//每页显示多少条
    var status: String? = null,//活动状态:0：下架； 1：上架
)
data class HistoryMsgReq(
    val chatType: String,//群聊还是私聊 1-群聊，2-私聊
    val groupId: String?=null,	//群聊Id
    val offset: String,//offset最后一条数据的id
    val userId: String?=null,//from用户的ID（APP为当前用户，live端为查看的用户ID）
    val searchId: String?=null,//to用户的ID（APP为当前用户，live端为查看的用户ID）
    val searchType: String="3",//查询类型（判断是用户还是直播端，普通用户查看不了被删除的消息）， live-2, APP-3
    val size: Int=50,
    val type: String="0"//消息类型，0-历史消息，1-离线消息
)
data class DelMsgBean(
    val anchorId: String
)
/**
 * 修改头像或者名称
 */
@Keep
data class InfoReq(
    var name: String? = null,//昵称
    var head: String? = null,//头像
)
/**
 * 提交反馈
 */
@Keep
data class FeedbackReq(
    var feedbackType: Int = 0,//反馈类型：1：更新问题，2：直播相关，3：产品体验，4：聊天相关，5：比赛相关，6：其他,可用值:1,2,3,4,5,6
    var feedbackContent: String = "",//反馈内容
    var feedbackImage: ArrayList<String> = arrayListOf(),//图片路径集合
) : Serializable




