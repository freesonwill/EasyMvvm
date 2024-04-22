package com.xcjh.base_lib.bean

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

@Keep
data class MyRecordPages<T>(
    val current: String = "",
    val records: ArrayList<T> = arrayListOf(),
    val empty: Boolean = false,
    val pages: String = "",
    val size: String = "",
    val total: String = ""
)

//搜索收米号
@Keep
data class SearchUser(
    val current: String = "",
    val hitCount: Boolean = false,
    val pages: String = "",
    val records: ArrayList<Record>,
    val searchCount: Boolean = false,
    val size: String = "",
    val total: String = ""
)

@Keep
data class Record(
    val headPortrait: String = "",
    val headPortrait22: String = "http://mmbiz.qpic.cn/mmbiz/PwIlO51l7wuFyoFwAXfqPNETWCibjNACIt6ydN7vw8LeIwT7IjyG3eeribmK4rhibecvNKiaT2qeJRIWXLuKYPiaqtQ/0",
    val nickName: String = "",
    val userId: String = ""
)

//我的订阅方案

//方案
@Keep
data class Scheme(
    val planMatchVO: PlanMatchVO = PlanMatchVO(),
    val planVO: PlanVO? = PlanVO(),
    var isSelected: Boolean = false,
    var tabPos: Int = 1,
    val slothSimpleVO: SlothSimpleVO = SlothSimpleVO()
) : Serializable

//比赛展示对象
@Keep
data class PlanMatchVO(
    val awayTeamVO: AwayTeamVO = AwayTeamVO(),
    val competitionSimpleVO: CompetitionSimpleVO = CompetitionSimpleVO(),
    val dataId: String = "",
    val homeTeamVO: HomeTeamVO = HomeTeamVO(),
    val id: String = "",
    val matchTime: String = "0"
):Serializable

@Keep
data class PlanVO(
    val id: String = "",
    val lookNum: String = "",
    var looked: String = "",
    val matchId: String = "",
    val multiplier: String = "",
    val twoSelect: Boolean = false,//是否双选
    val result: String = "",
    val price: String = "",
    val status: String = "",
    val title: String = "",
    val type: String = "",
    val createTime: String = "",
    val overTime: String = "",
    val upDown: Int = 0,//状态 -1下架 1上架
    val userId: String = ""
):Serializable

//收米号对象
@Keep
data class SlothSimpleVO(
    val headPortrait: String = "",
    val id: String = "",
    val nickName: String = "",
    val seriesHitNum: String = "",
    val slothTypeCountVO: SlothTypeCountVO? = SlothTypeCountVO(),
    val subscribe: String = "",
    val tenHitNum: String = "",//近十命中数
    val tenPlanNum: String = "",//近十方案数必须大于10
    val userType: String = ""
):Serializable

//主队
@Keep
data class HomeTeamVO(
    val shortNameZh: String = "",
    val dataId: String? = null,
    val group: String? = null,
    val halfCourtScore: String? = null,
    val id: String? = null,
    val logo: String? = null,
    val nameZh: String? = null,
    val points: String? = null,
    val score: String? = null,
    val overTimeScore: String? = null ,  // 加时比赛比分 (120分钟，即包括常规时间比分)
    val penaltyScore: String? = null ,  // 点球比赛比分 (不包含常规时间及加时赛比分)
):Serializable
//客队
@Keep
data class AwayTeamVO(
    val dataId: String? = null,    // 三方数据id
    val group: String? = null,     // 比赛分组 不为0表示分组赛的第几组，1-A、2-B以此类推
    val halfCourtScore: String? = null,  // 半场比赛比分
    val id: String? = null,      // 队伍id
    val logo: String? = null,  // 球队logo
    val nameZh: String? = null,  // 中文名称
    val points: String? = null,    // 比赛积分排名
    val shortNameZh: String = "",
    val score: String? = null ,  // 当前比赛比分
    val overTimeScore: String? = null ,  // 加时比赛比分 (120分钟，即包括常规时间比分)
    val penaltyScore: String? = null ,  // 点球比赛比分 (不包含常规时间及加时赛比分)
):Serializable

//最后一次观看广告的对象
@Keep
data class AddSeeLastVoList(
    val advType: Int? = null,    // 广告类型1开屏广告2比赛直播贴片广告3树懒号广告4资讯广告5比赛详情-情报6比赛详情-会员
    val lastTime: String? = null,     // 最后一次观看广告时间
):Serializable


//赛事集合
@Keep
data class CompetitionSimpleVO(
    val dataId: String = "",
    val id: String = "",
    val logo: String = "",
    val name: String = "",
    val nameEn: String? = null,
    val nameZh: String? = null,
    val nameZht: String? = null,//赛事繁体名称
    val shortNameEn: String? = null,
    val shortNameZh: String? = null,
    val shortNameZht: String? = null,
    val matchCount: String = "",//赛事比赛场次
    var isSelected: Boolean = false,   // 自己维护
    val type: String = ""
):Serializable



@Keep
data class SlothTypeCountVO(
    val countType: String = "",//统计类型 1-十场 2-周 3-月 4-季
    val hitNum: String = "",
    val hitRate: String = "",
    val id: String = "",
    val missNum: String = "",
    val planNum: String = "",
    val type: String = "",
    val userId: String = ""
):Serializable


//我的订阅收米号
@Keep
data class MySubUser(
    var redPoint: String = "",
    val salePlanNum: String = "",
    val subscribeCount: String = "",
    val userSimpleVO: UserSimpleVO
)

//用户对象
@Keep
data class UserSimpleVO(
    val headPortrait: String = "",
    val id: String = "",
    var follow: Boolean,
    val myself: Boolean? = false,
    val levelId: Int = 0,//用户等级
    val nickName: String = "",
    val userType: String = ""
)

//查询推荐赛程
@Keep
data class RecommendSchedule(
    val planMatchVO: PlanMatchVO = PlanMatchVO(),
    val slothSimpleVOList: ArrayList<SlothSimpleVO> = arrayListOf()
)
//查询推荐赛程
@Keep
data class RecommendMatch(
    val awayLogo: String,
    val awayName: String,
    val awayScore: String,
    val homeLogo: String,
    val homeName: String,
    val homeScore: String,
    val matchId: String,
    val matchTime: Long,
    val name: String,
    val planSum: String,
    val statusId: String
)

//社区赛程
@Keep
data class CommunitySchedule(
    var isExpand: Boolean=true,
    val communityScheduleDetailsVOList: ArrayList<CommunityScheduleDetailsVO> = arrayListOf(),
    val matchDay: String = "",
    val matchNum: String = "",
    val week: String = ""
)


@Keep
data class CommunityScheduleDetailsVO(
    val awayPosition: String = "",//
    val awayTeamVO: AwayTeamVO = AwayTeamVO(),
    val homeTeamVO: HomeTeamVO = HomeTeamVO(),
    val competitionSimpleVO: CompetitionSimpleVO = CompetitionSimpleVO(),
    val dataId: String = "",
    val homePosition: String = "",//
    val id: String = "",
    val lotteryNumber: String = "",
    val matchTime: String = "",
    val statusId: Int = 0,
    val runTime: String = "",
    val planNum: String = ""
)

//查询赛程赛事过滤
@Keep
data class ScheduleFilter(
    val competitionSimpleVOList: List<CompetitionSimpleVO> = listOf(),
    val initial: String = "",//key  A B C D
    val hasHot: Boolean = false,     // 自己维护
)

//查询赛事列表
@Keep
data class ScheduleFilterList(
    val value: List<ScheduleBean> = listOf(),
    val key: String = "",//key  A B C D
    val hasHot: Boolean = false,     // 自己维护
)

data class ScheduleBean(
    val competitionId: Int,
    val count: Int = 0,
    val countryId: String = "",
    val hot: Boolean = false,
    val matchId: String = "",
    var isSelected: Boolean = false,   // 自己维护
    val name: String = ""
)

//社区榜单
@Keep
data class CommunityTop(
    val monthHitList: List<HotHit> = listOf(),
    val seasonHitList: List<HotHit> = listOf(),//季度
    val seriesHitList: List<HotHit> = listOf(),//连续中
    val weekHitList: List<HotHit> = listOf()
)

//榜单
@Keep
data class HotHit(
    val number: String = "",
    val salePlanNum: String = "",
    // val userSimpleVO: UserSimpleVO = UserSimpleVO()
    val slothSimpleVO: SlothSimpleVO = SlothSimpleVO(),
    override var itemType: Int = 0,//0正常，1 更多
): MultiItemEntity


//查询榜单明细 命中
@Keep
data class TopHitDetails(
    val hitRateDetailsVOList: List<HitRateDetailsVO> = listOf(),
    val userHitRateDetailsVO: UserHitRateDetailsVO
) {
    @Keep
    data class HitRateDetailsVO(
        val salePlanNum: String = "",
        val slothSimpleVO: SlothSimpleVO = SlothSimpleVO()
    )

    @Keep
    data class UserHitRateDetailsVO(
        val countType: String = "",
        val hitNum: String = "",
        val hitRate: String = "",
        val id: String = "",
        val missNum: String = "",
        val planNum: String = "",
        val rank: String = "",
        val salePlanNum: String = "",
        val sloth: String = "",
        val userSimpleVO: UserSimpleVO
    )
}

//查询榜单明细 连中
@Keep
data class TopSeriesDetails(
    val seriesDetailsVOList: List<SeriesDetailsVO> = listOf(),
    val userSeriesDetailsVO: UserSeriesDetailsVO = UserSeriesDetailsVO()
)

@Keep
data class SeriesDetailsVO(
    val salePlanNum: String = "",
    val seriesHitNum: String = "",
    val subscribe: String = "",
    val userSimpleVO: UserSimpleVO
)

@Keep
data class UserSeriesDetailsVO(
    val headPortrait: String = "",
    val id: String = "",
    val nickName: String = "",
    val planNum: String = "",
    val rank: String = "",
    val seriesHitNum: String = "",
    val sloth: String = ""
)

@Keep
data class CenterTabBean(
    val id: Int? = 0,
    var num: String? = "",
    val name: String? = "",
    val img: Int = 0,
    var selected: Boolean = false,
)

//文章详情
@Keep
data class ArticleBean(
    val articleMatchVOList: List<ArticleMatchVO>,//比赛对象集合
    val articleVO: ArticleVO,//文章对象
    val planListVO: Scheme,//方案对象
    val userSimpleVO: UserSimpleVO//用户对象
)

@Keep
data class ArticleMatchVO(
    val articleId: String,
    val awayLogo: String,
    val awayName: String,
    val awayTeamId: String,
    val competitionId: String,
    val competitionName: String,
    val homeLogo: String,
    val homeName: String,
    val homeTeamId: String,
    val matchId: String
)

//文章对象
@Keep
data class ArticleVO(
    var commentCount: String = "",
    val content: String,
    val contentColor: String,
    val id: String,
    val imgList: ArrayList<String>,
    val publisherTime: String,
    val title: String,
    val voideContent: String,//宽高比
    val titleColor: String
)

//比赛关联文章
@Keep
data class ArticleScheduleDetailBean(
    var isExpand: Boolean =true,
    val articleScheduleDetailsVOList: List<ArticleScheduleDetailsVO>,
    val matchDay: String,
    val matchNum: String,
    val week: String
)

//热门话题比赛明细展示对象
@Keep
data class ArticleScheduleDetailsVO(
    var isSelect: Boolean,
    val awayPosition: String,
    val awayTeamScore: String,
    val awayTeamVO: AwayTeamVO,
    val competitionSimpleVO: CompetitionSimpleVO,
    val homePosition: String,
    val homeTeamScore: String,
    val homeTeamVO: HomeTeamVO,
    val id: String,
    val matchTime: String,
    val runTime: String?,
    val statusId: Int// 1 未开赛，2 上半场，3 中场， 4 下半场， 5 加时赛， 7 点球决赛， 8 完场， 9 推迟， 10 中断， 11 腰斩, 12 取消， 13 待定
)

//文章热门话题展示对象
@Keep
data class HotArticleBean(
    val articleNumber: Int,
    val awayLogo: String,
    val awayName: String,
    val awayTeamId: String,
    val homeLogo: String,
    val homeName: String,
    val homeTeamId: String,
    val level: Int,
    val matchId: String
)

//文章列表
data class ArticleListBean(
    val articleMatchVO: ArticleMatchVO? = null,//比赛对象
    val articlePlanVO: ArticlePlanVO? = null,//方案对象
    val articleVO: ArticleVO,
    val userSimpleVO: UserSimpleVO
)

//方案对象
@Keep
data class ArticlePlanVO(
    val awayName: String,
    val awayTeamId: String,
    val homeName: String,
    val homeTeamId: String,
    val id: String,
    val matchId: String
)

//评论
@Keep
data class CommentBean(
    val content: String,
    val createTime: String,
    val id: String,//评论ID
    val title: String,
    val sourceId: String,//来源ID
    val sourceType: Int//来源类型 1->咨询 2->文章
)

//评论回复列表
data class CommentListBean(
    val commentVO: CommentVO,
    val replyList: List<CommentVO>
)

data class CommentVO(
    val content: String,
    val createTime: String,
    val id: Int,
    val level: Int,
    val like: Boolean,
    val likes: Int,
    val parentId: Int,
    val rootParentId: Int,
    val sourceId: Int,
    val sourceType: Int,
    val threadStarter: Boolean,
    val toComment: ToComment,
    val userId: Int,
    val userSimpleVO: UserSimpleVO
)

data class ToComment(
    val content: String,
    val createTime: String,
    val id: Int,
    val level: Int,
    val like: Boolean,
    val likes: Int,
    val parentId: Int,
    val rootParentId: Int,
    val sourceId: Int,
    val sourceType: Int,
    val threadStarter: Boolean,
    val toComment: ToComment?,
    val userId: Int,
    val userSimpleVO: UserSimpleVO
)

//商品列表
data class GoodsBean(
    val description: String = "",
    val icon: String = "",
    var id: String = "",
    val name: String = "",
    val price: String = "",
    val remark: String = "",
    val stockNum: String = "",//剩余
    val stockTotal: String = ""
) : Serializable

/**
"info": "{\"账号\":18,\"密码\":19}",
 */
//兑换详情
data class ExchangeGoodsBean(
    val exchgeTime: String,
    val goodsId: String,
    val id: String,
    val info: String,//商品信息json
    val name: String,
    val price: String?,
    val remark: String,
    val type: Int,//1未兑换 2兑换
    val userId: String
) : Serializable

//社区主页公告展示对象
data class CommunityNoticeBean(
    val noticeVOList: List<NoticeVO>,
    val rotationChartVOList: List<RotationChartVO>
)

//公告展示对象
data class NoticeVO(
    val content: String,
    val createTime: String,
    val id: String,
    val title: String
)

//轮播图展示对象
data class RotationChartVO(
    val id: String,
    val informationId: String,
    val noticeId: String,
    val sort: Int,//排序
    val title: String,
    val upictureUrl: String
)

data class NewFocusUserList(
    val hasNext: Boolean,
    val userVOList: ArrayList<UserVO>
)

//关注的用户
data class UserVO(
    val headPortrait: String,
    val id: String,
    val levelId: Int,
    val nickName: String,
    val userType: Int
)

//关注的人 或者 自己的粉丝
data class FocusOrFansBean(
    val articleCommentNumber: String,
    val articleNumber: String,
    val fansNumber: String,
    var follow: Boolean,//我是否关注了此用户
    val followArticleNumber: String,
    val followNumber: String,
    val headPortrait: String,
    val id: String,
    val informationCommentNumber: String,
    val levelId: Int,
    val nickName: String
)

/**
 * 个人中心数据
 */
@Keep
data class PersonBean(
    val id: String = "0",
    val headPortrait: String = "0",
    val levelId: String = "0",
    val myself: Boolean = false,
    val nickName: String = "",

    var follow: Boolean = false,

    val fansNumber: String = "0",
    val followNumber: String = "0",

    val followArticleNumber: String = "0",
    val articleNumber: String = "0",
    val articleCommentNumber: String = "0",
    val informationCommentNumber: String = "0",

    )

data class ExCopyBean(
    var name: String?,
    var content: String?
)

//任务
data class TaskBean(
    val check: Boolean,
    val checkSwitch: Int,
    val dailyTaskList: List<DailyTask>,
    val difference: Int,
    val headPortrait: String,
    val integral: Int,
    val level: Int,
    val levelPointNum: Int,
    val min: Int,
    val otherTaskList: List<OtherTask>,
    val pointNum: Int,
    val seriesNum: Int
)

data class DailyTask(
    val createBy: String,
    val createTime: String,
    val doneNum: Int,
    val fulfil: Int,
    val id: Int,
    val pointNum: Int,
    val remarks: String,
    val taskType: Int,
    val title: String,
    val totalNum: Int,
    val updateBy: String,
    val updateTime: String
)

data class OtherTask(
    val createBy: String,
    val createTime: String,
    val doneNum: Int,
    val fulfil: Int,
    val id: Int,
    val pointNum: Int,
    val remarks: String,
    val taskType: Int,
    val title: String,
    val totalNum: Int,
    val updateBy: String,
    val updateTime: String
)
data class ConsultTabBeanData (
    val id: String="",
    val name: String="",
    val competitionId: String=""
)
//赛事筛选 / 国家筛选
data class MatchFilterList(
    val key: String,                     // 索引 A, B, C
    val value: List<MatchFilterCom> = listOf(),
)

data class MatchFilterCom(
    val competitionId: String = "",    //赛事id，查询国家时为null
    val matchId: String = "",    // 比赛id
    val countryId: String = "", //国家id，查询赛事时为null
    val hot: Any,                 // 是否为热门赛事 true 为热门赛事
    val name: String,             // 国家名称  西甲
    val count: Int=0,              //
    var isSelected: Boolean = false,   // 自己维护
)
//条件
data class ConditionData(
    val large: List<ConditionCommBean>?,   // 连续大
    val negative: List<ConditionCommBean>?,  // 连续负
    val small: List<ConditionCommBean>?,    // 连续小
    val transports: List<ConditionCommBean>?,   // 连续输
    val victory: List<ConditionCommBean>?,    // 连续胜
    val win: List<ConditionCommBean>?,       // 连续赢
)
//组装的条件列表
data class ConditionLData(
    val name: String="",//连续大
    val conditionName: String="",//large
    val list: List<ConditionCommBean>?,   // 连续大
)
data class  ConditionCommBean(
    val condition: String="",   // 筛选条件
    val total: Int=0,        // 总数
    var isSelected: Boolean = false    // 自己维护
)
//指数
data class MatchFilterHGData(
    val handicap: Float?=null,   // 盘口
    val total: Int=0,        // 总数
    var isSelected: Boolean = false    // 自己维护
)

//申请主播
data class ApplyAnchorData(
    val auditStatus: Int=0,      // 审核状态0申请审核中1申请主播成功2申请主播失败
    var reason: String? = null    // 驳回原因
)
//vip 续费列表
data class VipListData(
    val amount: Int?=0,      //
    val day: Int?=0,      //
    val id: Int=0,      //
    var isSelected: Boolean = false,
    var newsName: String? = null    //
)
//apply vip返回数据
data class VipEndData(
    var vipEndTime: String? = null    //
)

//广告任务数据
data class AdFreeAmountTask(
    var amount: Int?=null,
    var okTotal: Int?=null,
    var total: Int?=null,
)
//传递激励视频广告参数给后台
data class AdFreeCustomData(
    var type:Int?,//1 //看激励广告，获取收米币 2 比赛详情观看广告
    var other: String?=null,//其他
)