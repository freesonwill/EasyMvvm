package com.xcjh.base_lib.bean

import androidx.annotation.Keep
import java.io.Serializable

/**
 * 所有请求实体类 =======================================================================
 */


//搜索收米号
@Keep
data class SearchReq(
    var keyword: String? = "",
    var page: Page? = Page()
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

//查询社区赛程
@Keep
data class ScheduleReq(
    val competitionIds: ArrayList<Int> = arrayListOf(),//	赛事ID集合(短ID) []
    val publish: Int = 0//是否发布 0->不发布 1->发布
)

//获取系统消息的请求体
@Keep
data class SystemReq(
    val page: Page = Page(),
    val `where`: Where = Where()
) {
    @Keep
    data class Page(
        val current: Int = 0,
        val size: Int = 0
    )

    @Keep
    class Where
}

data class MineMsgReq(
    val messageSubType: String,   // MessageSubType
    val page: MineMsgPage,
)

data class MineMsgPage(
    val current: Int,
    val size: Int,
)

object MessageSubType {
    const val LIKES = "likes"
    const val COMMENTS = "comments"
}

data class MsgStateReq(
    val ids: ArrayList<String>,
)

//收藏
data class CollectReq(
    val collectStatus: Int,//收藏状态 0->收藏 1->取消收藏
    val id: Int,//球队id/赛事id
    val type: Int,//收藏类型 1->球队 2->赛事
)

/**
 * 发送验证码
 */
data class EmailCodeReq(
    val email: String,
    val type: String,//登录login,重置密码resetPwd ,重置邮箱resetEm
)

data class DecideCodeReq(
    val email: String,
    val code: String,//
    val type: String,//"获取验证码类型,登录login,重置密码resetPwd,修改密码modifyPwd,重置邮箱号resetEm,修改手机号updatePhone,修改手机号type=updatePhone"
)

/**
 *绑定邮箱
 */
data class BindEmailReq(
    val email: String,//
    val code: String,//
    // val confirmPassword: String="",//
    //  val password: String="",//
)

//赛事国家赛选参数
data class MatchFilterComReq(
    val type: Int = 100,//type 比赛状态；默认查询全部 全部：100； 进行中：200； 赛程： 300； 赛果：400； 关注：500,示例值(100)
    val tcType: Int = 0,   // 体彩参数 0全部 1竞彩 2北单 3足彩 5五大
    val time: String? = null,         // 时间
)

//筛选指数
data class MatchFilterHGReq(
    val handicapType: String? = null,   // 让球-as ，进球-bs
    val status: Int = 100,//全部：100； 进行中：200； 赛程： 300； 赛果：400； 关注：500
    val time: String? = null,  // 当前日期 如 2022-06-29
)

//比赛 关联 文章
@Keep
data class ArticleAddMatchReq(
    val competitionIds: ArrayList<Int> = arrayListOf(),//	赛事ID集合(短ID)
    val type: Int = 1//比赛状态 1->未开始 2->进行中 3->已结束
)

//发布帖子文章
@Keep
data class PostArticleReq(
    val planId: String?, //方案ID
    val title: String?, //
    val titleColor: String?, //
    val content: String?, //
    val contentColor: String?, //
    var voideContent: String? = null, //视频宽高比
    val matchIds: ArrayList<Int>? = null,//	赛事ID集合(短ID)
    var imgList: ArrayList<String>? = null,//图片集合
)

//评论点赞
@Keep
data class CommentLikeReq(
    val commentId: String = "",//评论id
    val type: Int = 0,//类型 0->取消点赞 1->点赞
)

//评论
data class CommentReq(
    val content: String,
    val parentId: Int,        // 回复的评论id
    val rootParentId: Int,    // 根评论id
    val sourceId: Int,//来源ID
    val sourceType: Int//来源类型 1->咨询 2->文章 3->公告
)

//评论
data class CommentReq2(
    val content: String,
    val parentId: String? = null,        // 回复的评论id
    val rootParentId: String? = null,    // 根评论id
    val sourceId: String,//来源ID
    val sourceType: Int//来源类型 1->咨询 2->文章 3->公告
)

//举报
data class ReportReq(
    val content: String?,
    val displayLocation: Int,//位置 1资讯 2收米号 3社区
    val reportId: String,//举报对象id 如:资讯ID 文章ID
    var upictureUrlList: List<Any>? = null//图片url集合
)

//关注
@Keep
data class FollowReq(
    val userId: String = "",//	用户Id
    val type: Int = 0,//类型 0->取消关注 1->关注
)

//任务
//id ：1聊天发言 2每日分享 3阅读资讯 4 实名认证 5 社区发帖 6评论回帖
// 7收米币充值
@Keep
data class TaskReq(
    val id: Int,
    val pointNum: Int?,//积分 7收米币充值数额
)

//js交互 {\"match\":false,\"number\":false,\"place\":false,\"type\":\"recent\"}"
data class JsReq(
    var match: Boolean = false,
    var number: Boolean = false,
    var place: Boolean = false,
    var type: String? = "recent",
)

//筛选条件
data class FilterReq(


    var size: Int = 20, //每页页码数据量
    var current: Int = 1, //当前页码
    var status: Int? = 100,//全部：100； 进行中：200； 赛程： 300； 赛果：400； 关注：500 默认不传为查询全部

    var time: String? = null,
    var tcType: Int? = null,      // 体彩参数 0完整 1竞彩  2北单  3足彩  5五大
    var competitionId: MutableList<String>? = null,//赛事id

    var conditionType: String? = null,   // transports-连续输  win-连续赢 large-连续大  small-连续小 victory-连续胜  negative——连续负
    var conditionList: List<Int>? = null,   // 条件集合 如[开始数字，结束数字]  3-4 拆开 [3, 4]

    var handicapType: String? = null,      // 让球 as ; 进球 bs
    var lists: List<String>? = null,        // 指数集合
    var indexIsNull: Boolean? = null,        // 指数是否包含null


    var countryId: MutableList<String>? = null, // 国家id
    //var choiceType: Boolean? = null,        // 快捷选择 0全部 1五大 2情报 3竞彩
    var sortType: Int? = 0,        // 排序type 0时间排序 1赛事排序
    var name: String?=null, // 球队/赛事名称
) : Serializable

//申请主播
@Keep
data class AnchorApplyDtoReq(
    val userUrl: String? = null,//Url视频解说
    val userVideo: String? = null,//用户视频
)
//添加广告观看记录
//观看广告请求对象
@Keep
data class UserSeeAdvDto(
    val advType: Int? = null,//广告类型1开屏广告2比赛直播贴片广告3树懒号广告4资讯广告5比赛详情-情报6比赛详情-会员
    val dataId: String? = null,//目标ID:adv_type=2/5/6传比赛ID,其他的不传
)
//获取首页关注比赛列表
@Keep
data class MatchRequest(
    val current: Int,
    val size: Int?,
    val name: String? = null,    // 搜索名称
    val source: String? = null,   // 查询类型 hot-热门比赛 , follow-关注, 单个赛事传id
    val status: Int? = null,     // 比赛状态 全部：100； 进行中：200； 赛程： 300； 赛果：400； 关注：500
    val time: String? = null,    // 指定日期
)
//关注比赛
@Keep
data class MatchFollowReq(
    val competitionListId: String,  // 比赛id
    val followState: Boolean,      //  关注true、取消关注false
)