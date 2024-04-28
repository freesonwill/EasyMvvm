package com.xcjh.app.bean

import android.util.Log
import androidx.annotation.Keep
import com.drake.brv.BindingAdapter
import com.drake.brv.item.ItemAttached
import com.drake.brv.item.ItemHover
import com.xcjh.base_lib.bean.ListDataUiState
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


@Keep
data class MyListPages<T>(
    val current: Int = 0,
    val `records`: ArrayList<T> = arrayListOf(),
    val empty: Boolean = false,
    val pages: Int = 1,
    val size: Int = 0,
    val total: Int = 0
)
data class AutherInfoBean(
    val fansCount: Int,
    val firstMessage: String,
    val focus: Boolean,
    val head: String,
    val id: Int,
    val nickName: String,
    val notice: String,
    val titlePage: String
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
    var tel: String? = "",//	手机号
    var email: String? = "",//	邮箱
    var name: String? = "",//	昵称
    var head: String? = "",//	头像
    var lvNum: String? = "1",//	等级
    var lvName: String? = "",//	等级名称
    var growthValue: String? = "0",//	成长值
    var growthValueNext: String? = "0",//	下一阶段成长值
    var registerAddr: String? = "",//	注册地址
    var ynForbidden: String? = "",//	是否禁言 0否 1普通禁言 2永久禁言
    var forbiddenDay: String? = "",//	禁言天数
    var forbiddenTime: String? = "",//	禁言期限
    var forbiddenDescp: String? = "",//	禁言原因
    var createTime: String? = "",//	创建时间
    var updateTime: String? = "",//	修改时间


) : Serializable

/**
 * 首页广告banner
 */
@Keep
data class AdvertisementBanner(
    var id: String = "",
    var name: String = "",//广告名称
    var text: String = "",//文字
    var imgUrl: String = "",//图片地址
    var targetUrl: String = "",//跳转地址
    var list: ArrayList<AdvertisementBanner> = arrayListOf(),//问题ID集合
) : Serializable

data class CountryListBean(
    val cn: String,
    val dialingCode: String,
    val en: String,
    val zht: String,
    val full: String,
    val icon: String,
    var shortName: String
)

/**
 * 赛程对象
 */
@Keep
data class MatchBean(
    var competitionId: String = "",//赛事ID
    var competitionName: String = "",//赛事
    var matchId: String = "",//比赛id
    var matchType: String = "",//比赛类型：1：足球；2：篮球,可用值:1,2
    var matchTime: String = "",//比赛时间
    var visbleTime: Int = 0,//是否显示比赛时间，0是需要判断，1是显示，2是不显示
    var homeName: String = "",//主队名称
    var homeLogo: String = "",//主队Logo
    var homeScore: String = "",//主队比分
    var homeHalfScore: String = "",//主队半场比分
    var awayName: String = "",//客队名称
    var awayLogo: String = "",//客队Logo
    var awayScore: String = "",//客队比分
    var awayHalfScore: String = "",//客队半场比分
    //篮球状态:0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
    // 1 未开赛;2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定;
    // 足球状态码:0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
    var status: String = "0",
    var focus: Boolean = false,//是否关注
    var anchorList: ArrayList<AnchorBean> = arrayListOf(),//正在进行比赛对应的主播列表
    var list: ArrayList<MatchBean> = arrayListOf(),//首页热门比赛
    var matchData: ArrayList<ConditionsBean> = arrayListOf(),//比赛是否有赛况、阵容、指数
    var runTime: String? = "0",//比赛进行时间（分钟）进行中足球比赛有此信息
    var ishsow: Boolean = true,
    var pureFlow:Boolean=false,//是否是纯净流

) : Serializable

data class InitialLocation(
    val initial: String,
    val list: List<Location>
)

//首页数据组装
data class  MainDataBean(
    //广告
    var advertisement: ArrayList<AdvertisementBanner> =ArrayList<AdvertisementBanner>(),
    //热门比赛集合
    var match:ArrayList<MatchBean> =ArrayList<MatchBean>(),
    //正在直播的主播
    var list:ArrayList<BeingLiveBean> =ArrayList<BeingLiveBean>()

): Serializable

data class Location(
    val code: String,
    val name: String,
    val pinyin: String,
    val label: String
)

@kotlinx.serialization.Serializable
data class CityModel(
    var initial: String = "",
    var list: List<City> = listOf()
) {
    @kotlinx.serialization.Serializable
    data class City(
        var code: String = "",
        var cnname: String = "",
        var enname: String = "",
        var zhtname: String = "",
        var pinyin: String = "",
        var label: String = ""
    )


    // 悬停要求实现接口
    // data class 会自动重写equals方法, 方便匹配索引查询
    data class CityLetter(val letter: String, override var itemHover: Boolean = true) : ItemHover
}

/**
 * 热门赛程对象
 */
@Keep
data class HotMatchBean(
    val competitionId: String,
    val competitionName: String,
    val matchCount: Int,
    val matchType: String
) : Serializable

/**
 * 群聊消息
 */
data class MsgBean(
    var fromId: String? = "",//发送用户ID
    var avatar: String? = "",//发送者头像
    var nick: String = "",//发送者昵称
    val level: String? = "0",//级别
    var content: String = "",//消息内容
    var chatType: Int? = 0,//聊天类型;(如1:公聊、2私聊)
    var cmd: Int? = 0,
    var anchorId: String? = "",//主播ID
    var createTime: Long? = 0,
    val creator: String? = "",
    val delFlag: Int? = 0,//APP用户是否删除 1删除 0正常
    val groupId: String? = "",//群组ID
    var id: String? = "",
    val identityType: Int? = 0,//发送者身份身份(0：普通用户，1主播 2助手(占无) 3助理 )
    var msgType: Int? = 0, //消息类型，文字：0， 图片：1  3视频  6通知
    val readable: Int? = 0,//是否已读：0 未读 1 已读
    var sent: Int? = 0,//是否已发送: 0 正在发送 1 已发送 2 发送失败
    val toId: String? = "",
    val updateTime: String? = "",
    var lastShowTimeStamp: Long = 0,
    val updater: String? = ""
) : ItemAttached {
    var visibility: Boolean = false // 显示隐藏
    override fun onViewAttachedToWindow(holder: BindingAdapter.BindingViewHolder) {
        //由隐变显
        visibility = true
    }

    override fun onViewDetachedFromWindow(holder: BindingAdapter.BindingViewHolder) {
        //由显变隐
        visibility = false
    }
}
/**
 * 群聊首条消息
 */
data class FirstMsgBean(
    var fromId: String? = "",//发送用户ID
    var avatar: String? = "",//发送者头像
    var nick: String = "",//发送者昵称
    val level: String? = "0",//级别
    var content: String = "",//消息内容
    var chatType: Int? = 0,//聊天类型;(如1:公聊、2私聊)
    var cmd: Int? = 0,
    var anchorId: String? = "",//主播ID
    var createTime: Long? = 0,
    val creator: String? = "",
    val delFlag: Int? = 0,//APP用户是否删除 1删除 0正常
    val groupId: String? = "",//群组ID
    var id: String? = "",
    val identityType: Int? = 0,//发送者身份身份(0：普通用户，1主播 2助手 3运营)
    var msgType: Int? = 0, //消息类型，文字：0， 图片：1
    val readable: Int? = 0,//是否已读：0 未读 1 已读
    var sent: Int? = 0,//是否已发送: 0 正在发送 1 已发送 2 发送失败
    val toId: String? = "",
    val updateTime: String? = "",
    var lastShowTimeStamp: Long = 0,
    val updater: String? = ""
) : ItemAttached {
    var visibility: Boolean = false // 显示隐藏
    override fun onViewAttachedToWindow(holder: BindingAdapter.BindingViewHolder) {
        //由隐变显
        visibility = true
    }

    override fun onViewDetachedFromWindow(holder: BindingAdapter.BindingViewHolder) {
        //由显变隐
        visibility = false
    }
}
/**
 * 群聊最后占位消息
 */
data class BottomMsgBean(
    var isBottom: Boolean = true,//
) : ItemAttached {
    var visibility: Boolean = false // 显示隐藏
    override fun onViewAttachedToWindow(holder: BindingAdapter.BindingViewHolder) {
        //由隐变显
        visibility = true
    }

    override fun onViewDetachedFromWindow(holder: BindingAdapter.BindingViewHolder) {
        //由显变隐
        visibility = false
    }
}
data class FeedBackBean(
    val type: Int,//通知类型(0系统通知 1反馈结果 2禁言通知 3解禁通知)
    val title: String,
    val notice: String,
    val createTime: String,
    val bizId: String,
    val readFlag: String,//	反馈类型：1：更新问题，2：直播相关，3：产品体验，4：聊天相关，5：比赛相关，6：其他,可用值:1,2,3,4,5,6
    val id: Int
)

/**
 * 群聊和私聊消息内容
 *
 */
data class ChatMyMsgBean(
    var id: String? = "",   // 消息ID
    var chatType: Int? = 1,   // 聊天类型，群聊：1， 私聊：2
    var msgType: Int? = 0, //消息类型，文字：0， 图片：1
    var cmd: Int? = null,   // 6，对应的cmd：5
    var content: String? = "",   // 消息内容
    var createTime: String? = "",   // 消息时间
    var createTime1: String? = "",   // 消息时间
    var anchorId: String? = "", var from: String? = ""  // 发送者ID

)

@Keep
data class FriendListBean(
    var anchorId: String,
    var fans: Int,
    var head: String = "",
    var pinyin: String = "",
    var liveId: Int,
    var nickName: String = "",
    var shortName: String = ""

)

@Keep
data class FriendModel(
    var initial: String = "",
    var list: List<Friend> = listOf()
) {
    data class Friend(
        var anchorId: String,
        var fans: Int,
        var head: String = "",
        var pinyin: String = "",
        var liveId: Int,
        var nickName: String = ""
    ) : Serializable

    // 悬停要求实现接口
    // data class 会自动重写equals方法, 方便匹配索引查询
    data class FriendLetter(val letter: String, override var itemHover: Boolean = true) : ItemHover
}

@Keep
data class LetterBean(
    var cn: String, var en: String, var phone_code: String = ""
) : Serializable

@Keep
data class MsgListBean(
    val avatar: String? = null,
    var content: String? = null,
    val createTime: Long,
    val msgType: Int? = null,
    val fromId: String? = null,
    val anchorId: String? = null,
    val id: String? = null,
    val nick: String? = null,
    var noReadSum: Int
) : Serializable

/**
 * 主播对象
 */
@Keep
data class AnchorBean(
    var liveId: String = "",//直播间ID
    var userId: String = "",//主播ID
    var nickName: String = "",//主播昵称
    var userLogo: String = "",//主播头像


) : Serializable


/**
 * 比赛是否有赛况、阵容、指数
 */
@Keep
data class ConditionsBean(
    var hasStata: Boolean = false,//是否有赛况
    var hasLineup: Boolean = false,//是否有阵容
    var hasOdds: Boolean = false,//是否有指数


) : Serializable

/**
 * 首页列表文字
 */
@Keep
data class MainTxtBean(
    var txt: String = "111",
    var list: ArrayList<BeingLiveBean> = arrayListOf(),

    ) : Serializable


/**
 * 首页最后一个类型正在直播对象 正在直播
 */
@Keep
data class BeingLiveBean(
    var id: String = "",
    var userId: String = "",
    var nickName: String = "",//昵称
    var userLogo: String = "",//头像
    var titlePage: String = "",//封面
    var notice: String = "",//公告
    var firstMessage: String = "",//首条消息
    var compositionId: String = "",//赛事ID
    var competitionName: String = "",//赛事名称
    var matchId: String = "",//比赛ID
    var matchType: String = "",//比赛类型 1足球，2篮球,可用值:1,2
    var matchTime: String = "",//比赛时间
    var homeTeamName: String = "",//主队名称
    var homeTeamLogo: String = "",//主队LOGO
    var awayTeamName: String = "",//	客队名称
    var awayTeamLogo: String = "",//	客队LOGO
    var sourceUrl: String = "",//	来源地址
    var playUrl: String = "",//	播放地址
    var openTime: String = "",//	开播时间
    var closeTime: String = "",//	关播时间
    var liveStatus: String = "",//	直播状态,1:未开始，2：直播中，3：已关播,可用值:1,2,3
    var hotValue: Int = 0,//	总热度
    var onlineUser: Int = 0,//	在线人数
    var hottest: Boolean = false,//	是否是热门
    var newest: Boolean = false,//	是否是最新
    var awayTeamNameEn: String = "",//	客队英文名字
    var awayTeamNameZht: String = "",//	客队繁体名字
    var homeTeamNameEn: String = "",//	主队英文名
    var homeTeamNameZht: String = "",//	主队繁体名
    var competitionNameEn: String = "",//	赛事英文
    var competitionNameZht: String = "",//	赛事英文繁体
    var pureFlow:Boolean=false,//是否是纯净流
    var homeScore:String="0",//主队比分
    var awayScore:String="0",//客队比分
    //篮球状态:0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;
    // 1 未开赛;2 第一节;3 第一节完;4 第二节;5 第二节完;6 第三节;7 第三节完;8 第四节;9 加时;10 完场;11 中断;12 取消;13 延期;14 腰斩;15 待定;
    // 足球状态码:0 比赛异常，说明：暂未判断具体原因的异常比赛，可能但不限于：腰斩、取消等等，建议隐藏处理;1 未开赛;2 上半场;3 中场;4 下半场;5 加时赛;6 加时赛(弃用);7 点球决战;8 完场;9 推迟;10 中断;11 腰斩;12 取消;13 待定
    var status: String = "",//比赛状态




) : Serializable

/**
 * 足球比赛详情页面群聊消息
 */
@Keep
data class MessageBean(
    var userId: String = "",//用户ID
    var userType: String = "",//用户类型
    var nickName: String = "",//用户昵称
    var content: String = "",//消息内容
) : Serializable

/**
 * 首页新闻列表
 */
@Keep
data class NewsBean(
    var id: String = "",// ID
    var title: String = "",//标题
    var tournament: String = "",//联赛名称
    var content: String = "",//新闻内容
    var sourceUrl: String = "",//源地址
    var publishTime: String = "",//发布时间
    var pic: String = "",//图片

) : Serializable


/**
 * 个人中心活动列表
 */
@Keep
data class EventsBean(
    var id: String = "",// ID
    var title: String = "",// 标题
    var mainPic: String = "",// 封面主图
    var content: String = "",// 文章内容
    var sortOrder: String = "",// 排序
    var updateTime: String = "",// 发布时间
    var status: String = "",// 状态：1：上架；0下：架


) : Serializable

/**
 * 我关注的主播列表
 */
@Keep
data class FollowAnchorBean(
    var anchorId: String = "",// ID
    var nickName: String = "",// 昵称
    var head: String = "",// 头像
    var fans: String = "",// 粉丝数
    var liveId: String = "",// 正在直播ID
    var matchId: String = "",//比赛ID
    var matchType: String,////比赛类型 1足球，2篮球,可用值:1,2
    var isFollow: Boolean = true//是否关注
) : Serializable

/**
 * 联系我们的问题类型
 */
@Keep
data class ContactBean(
    var id: Int = 0,//反馈类型：1：更新问题，2：直播相关，3：产品体验，4：聊天相关，5：比赛相关，6：其他,可用值:1,2,3,4,5,6
    var name: String = "",//
    var select: Boolean = false,//
) : Serializable

/**
 * 比赛详情界面主播tab下主播详情数据
 */
@Keep
data class DetailAnchorBean(
    var id: String = "",// ID
    var nickName: String = "",// 昵称
    var head: String = "",// 头像
    var notice: String? = "",// 公告
    var titlePage: String? = "",// 直播封面
    var firstMessage: String? = "",// 开播首条聊天
    var focus: Boolean = false,// 是否关注
    var fansCount: String? = "0",// 粉丝数
) : Serializable

/**
 * notice
 */
@Keep
data class NoticeBean(
    var id: String? = "",// ID
    var notice: String = "",// 公告
    var isOpen: Boolean = false,// 是否展开
    override var itemHover: Boolean = true,
) : ItemHover, Serializable

/**
 * 比赛详情界面阵容tab下主播阵容数据
 */
data class OddsBean(
    var euInfo: ArrayList<OddsDetailBean>? = arrayListOf(),
    var asiaInfo: ArrayList<OddsDetailBean>? = arrayListOf(),
    var bsInfo: ArrayList<OddsDetailBean>? = arrayListOf(),
    var crInfo: ArrayList<OddsDetailBean> = arrayListOf(),
) : Serializable

@Keep
data class OddsDetailBean(
    var close: Int = 0,// 是否封盘：0-否，1-是
    var companyId: String = "",// ID
    var companyName: String = "",//
    var firstHomeWin: String = "",//
    var firstAwayWin: String = "",//
    var firstDraw: String = "",//
    var firstLossRatio: String = "",//
    var currentHomeWin: String = "",//
    var currentAwayWin: String = "",//
    var currentDraw: String = "",//
    var currentLossRatio: String = "",//
    var status: String = "",//
) : Serializable

/**
 * 比赛详情界面跑马灯文字数据
 */
@Keep
data class ScrollTextBean(
    var id: Int = 0,// ID
    var name: String = "",// 名称
    var text: String = "",// 文字
    var imgUrl: String = "",// 图片链接
    var targetUrl: String = "",// 目标链接
) : Serializable

/**
 * 文字直播
 */
@Keep
data class LiveTextBean(
    var data: String = "",// 事件内容
    var main: Int = 0,// 是否重要事件，1-是、0-否
    var position: Int = 0,// 事件发生方，0-中立、1-主队、2-客队
    var time: String = "",// 事件时间(分钟)
    var type: Int = 0,// 类型，详见状态码->技术统计
) : Serializable

/**
 * 足球重要事件
 */
@Keep
data class IncidentsBean(
  /*  val assist1Id: Int? = 0,
    val assist1Name: String = "",//助攻球员名称
    val assist2Id: Int? = 0,
    val assist2Name: String = "",*/
    var awayScore: Int = 0,//
    var homeScore: Int = 0,//
    val inPlayerId: String? = "",//换上球员ID
    val inPlayerName: String? = "",//
    val outPlayerId: String? = "",//
    val outPlayerName: String? = "",//换下球员名称
    val playerId: String? = "",//事件相关球员id，可能不存在
    val playerName: String? = null,//事件相关球员名称，可能不存在
    val position: Int = 0,//事件发生方，0-中立、1-主队、2-客队
    val reasonType: String? = "",
    val second: Int = 0,//时间(秒)
    val time: Int = 0,//时间(分钟)
    val type: Int = 0,
    val varReason: Int? = 0,//VAR原因（VAR事件存在）
    val varResult: Int? = 0//VAR结果（VAR事件存在）
) : Serializable

/**
 * 比赛详情界面详情接口
 *
 */
@Keep
data class MatchDetailBean(
    //主播列表
    var anchorList: ArrayList<AnchorListBean>? = arrayListOf(),
    var awayHalfScore: Int = 0,//客队半场比分
    val awayLogo: String? = "",//客队Logo
    val awayName: String? = "",//客队名称
    var awayScore: Int? = 0,//客队比分

    var competitionId: String = "",// 赛事ID
    val competitionName: String = "",//赛事名称

    val focus: Boolean = false,// 是否关注

    var homeHalfScore: Int = 0,
    val homeLogo: String? = "",
    val homeName: String? = "",
    var homeScore: Int? = 0,

    val matchData: MatchDataBean = MatchDataBean(),

    var matchId: String = "",// 比赛ID
    val matchTime: String = "",// 时间
    var matchType: String = "1",// 类型1：足球；2：篮球,可用值:1,2
    var runTime: Int = 0,//比赛进行时间（分钟）进行中足球比赛有此信息
    var status: Int = 0
) : Serializable

@Keep
data class AnchorListBean(
    var liveId: String = "",
    var pureFlow: Boolean = false,//true 纯净流
    var isOpen: Boolean = true,//true 开播中 false已关播
    var userId: String? = null,
    var nickName: String = "",
    var userLogo: String = "",
    var hotValue: String = "",//热度
    var playUrl: String? = null,//播放地址
    var isSelect: Boolean = false,
) : Serializable/*, Comparable<PlayerBean> {
    override fun compareTo(other: PlayerBean): Int {
        return  other.score.toInt()-this.hotValue.toInt()
    }
}*/

@Keep
data class MatchDataBean(
    var hasStata: Boolean = false, //是否有赛况tab
    var hasLineup: Boolean = false,//是否有阵容tab
    var hasOdds: Boolean = false,  //是否有指数tab
) : Serializable

//切换tab
class TabBean(
    val type: Int = 0,
    val name: String = "",
)

/**
 * 获取app 升级信息
 */
@Keep
data class AppUpdateBean(
    var version: String = "",// 版本
    var forcedUpdate: String = "0",// 是否强制更新：0 ：不强制 1：强制
    var sourceUrl: String = "",//更新地址
    var remarks: String = "",//更新说明

) : Serializable

/**
 * 比赛阵容数据-足球
 */
@Keep
data class FootballLineupBean(
    var homeFormation: String? = "",//主队阵型
    var awayFormation: String? = "",//客队阵型
    var homeColor: String = "",//主队球衣颜色
    var awayColor: String = "",//客队球衣颜色
    var confirmed: String = "",//正式阵容，1-是、0-不是
    var refereeName: String? = "",//裁判名称
    var homeLogo: String = "",//主队logo
    var awayLogo: String = "",//客队logo
    var homeMarketValue: Int = 0,//主队市值
    var awayMarketValue: Int = 0,//客队市值
    var homeMarketValueCurrency: String? = "",//主队市值币种
    var awayMarketValueCurrency: String? = "",//客队市值币种
    var home: ArrayList<FootballPlayer> = arrayListOf(),//主队阵型球员列表
    var away: ArrayList<FootballPlayer> = arrayListOf(),//客队阵型球员列表
) : Serializable

/**
 * 比赛阵容数据-篮球
 */
@Keep
data class BasketballLineupBean(
    var home: ArrayList<BasketballTeamMemberBean> = arrayListOf(),//主队阵型球员列表
    var away: ArrayList<BasketballTeamMemberBean> = arrayListOf(),//客队阵型球员列表
) : Serializable

//球队信息
@Keep
data class MatchTeam(
    var logo: String? = "",//logo
    val name: String? = "",
) : Serializable

@Keep
data class FootballPlayer(
    val captain: Int = 0,//是否队长，1-是、0-否
    val first: Int = 0,//是否首发，1-是、0-否
    val id: Int = 0,
    val logo: String = "",
    val name: String = "",
    val nationalLogo: String = "",
    val position: String = "",//球员位置，F前锋、M中场、D后卫、G守门员、其他为未知
    val rating: String = "",
    val shirtNumber: Int = 0,//球衣号
    val teamId: Int = 0,
    val x: Int = 0,
    val y: Int = 0
) : Serializable

@Keep
data class BasketballTeamMemberBean(
    var number: String? = "",//球衣号
    var name: String = "",//名称
    var logo: String = "",//球员logo
    var data: PlayerBean = PlayerBean()
) : Serializable

@Keep
data class PlayerBean(
    var first: String = "0",//	是否是替补（1-替补，0-首发）
    var time: String = "0",//时间
    var rebound: String = "0",//篮板
    var assists: String = "0",//助攻
    var hitAndShot: String = "0",//投篮
    var score: String = "0",//得分
) : Serializable, Comparable<PlayerBean> {
    override fun compareTo(other: PlayerBean): Int {
        /*  return if (this.score == other.score) {
             // this.assists.compareTo(other.assists)
              other.assists.toInt() - this.assists.toInt()
          } else {
              other.score.toInt() - this.score.toInt()
          }*/
        return other.score.toInt() - this.score.toInt()
    }
}

/**
 * 篮球比赛得分
 */
@Keep
data class BasketballScoreBean(
    var homeScoreList: List<Int> = listOf(),//主队每节得分集合，
    var awayScoreList: List<Int> = listOf(),
    var homeOverTimeScoresList: List<Int>? = listOf(),//主队加时每节得分集合
    var awayOverTimeScoresList: List<Int>? = listOf(),
    val timeRemaining: Int = 0,//小节剩余时间(秒)
    var status: Int = 0,
)

/**
 * 热门赛程对象
 */
@Keep
data class StatusBean(
    val type: Int,//1:3分球进球数 2:2分球进球数 3:罚球进球数 4:剩余暂停数 5:犯规数 6:罚球命中率 7:总暂停数
    val home: Int,
    val away: Int,
) : Serializable

/**
 * 篮球技术统计
 */
@Keep
data class BasketballSBean(
    val away: BasketballSkill = BasketballSkill(),
    val home: BasketballSkill = BasketballSkill()
) : Serializable

@Keep
data class BasketballSkill(
    val assists: Int = 0,//助攻数
    val blocks: Int = 0,//盖帽数
    val defensiveRebound: Int = 0,//防守篮板
    val fouls: Int = 0,//个人犯规次数
    val hit: Int = 0,//命中次数
    val hit2: Int = 0,//
    val hit3: Int = 0,//
    val mistakes: Int = 0,//失误次数
    val offensiveRebound: Int = 0,//进攻篮板
    val penalty: Int = 0,//罚球投篮次数
    val penaltyHit: Int = 0,//罚球命中次数
    val rebound: Int = 0,//总的篮板
    val score: Int = 0,//
    val shot: Int = 0,//投篮次数
    val shot2: Int = 0,//
    val shot3: Int = 0,//
    val steals: Int = 0//抢断数
) : Serializable

@Keep
data class BasketballScore(
    val name: String = "",//
    val homeScore: String = "-",//
    val awayScore: String = "-"//
) : Serializable

/**
 * 推送状态
 */
@Keep
data class PushBean(
    val ynFollowMatch: Int = 1,//	是否开启关注比赛通知 1是 0否
    val ynLiveOpen: Int = 1,//是否开启主播开播通知 1是 0否
) : Serializable

/**
 * 推送错误时的返回值
 */
@Keep
data class PushErrBean(
    var name: String = "",// 推送类型 liveOpen:切换主播开播通知 followMatch:切换关注比赛通知
    var state: Boolean = false, //
) : Serializable

/**
 * 站外推送消息
 */
@Keep
data class PushMsgExtras(
    var matchId: String = "",// 比赛ID
    var isPureFlow: Boolean = false,// 是否纯净流
    var matchType: String = "",// 1足球 2篮球
    var anchorId: String? = "",// 主播id
    var liveId: String? = "", //聊天室id
) : Serializable

@Keep
data class Point(
    var x: Double=0.0,
    var y: Double=0.0
): Serializable

data class CaptchaGetIt(
    var originalImageBase64: String,//图表url 目前用base64 data
    var point: Point,
    var jigsawImageBase64: String,
    var token: String,// 获取的token 用于校验
    var result: Boolean,
    var opAdmin: Boolean,
    var secretKey:String //ase密钥
): Serializable

data class ImageResponse(
    var repCode: Int,
     var success:Boolean=false,
    var error:String=""
): Serializable

data class Input<T> (

    var repData: T? = null,
    var repCode: String? = null,
    var success: Boolean? = null,
    var error: Boolean? = null

): Serializable


data class CaptchaCheckIt(
    val captchaType: String,
    val token: String,
    val result: Boolean,
    val opAdmin: Boolean
): Serializable

data class WordCaptchaGetIt(
    val originalImageBase64: String,//图表url 目前用base64 data
    val result: Boolean,
    val token: String,// 获取的token 用于校验
    val secretKey:String,//ase密钥
    val wordList: MutableList<String>?

): Serializable



/**
 * 主播对当前用户禁言和踢出直播间信息
 */
@Keep
data class ProhibitionBean(
    var anchorId: String = "",// 主播id
    var userId: String = "",// 用户id
    var tickOutLeftTime: String = "",// 踢出房间剩余时间（分钟）
    var mute: Boolean = false,// 是否禁言
    var tickOut: Boolean = false,// 是否踢出房间
) : Serializable


/**
 * 比赛的日期
 */
@Keep
data class CompetitionBean(
    var time: String,//日期yyyy-MM-dd
    var num: Int=0,//数量

) : Serializable



