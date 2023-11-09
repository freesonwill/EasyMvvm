package com.xcjh.app.net

import com.xcjh.app.bean.*
import com.xcjh.base_lib.bean.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 *
 */
interface ApiComService {

    companion object {
        //预发布
        const val SERVER_URL = "https://app.cbd246.com/apis/"//app通用 测试
        const val SHARE_IP = "https://app.cbd246.com/"//比赛分享链接
        //test
        //测试服

        //  const val SERVER_URL = "http://192.168.101.15:6003/apis/"//app通用 开发
        //  const val SHARE_IP = "http://192.168.101.180:1820/"//比赛分享链接

        //正式
//         const val SERVER_URL_CENTER = "https://www.2web3.net/user-center/center/apis/"//中台正式
//         const val SERVER_URL = "https://holdem.news/apis/"//app通用 正式
//         const val SHARE_IP = "https://www.2web3.net/user-user/"//比赛分享链接

        const val SERVER_URL_EMAIL = "https://www.2web3.net/user-user/"//邮箱验证
    }


    //上传图片到oss
    @Multipart
    @POST("xcjh/admin/admin-base/upload-picture")
    suspend fun uploadOss(@Part file: MultipartBody.Part): ApiResponse<Any>

    //上传视频到oss
    @Multipart
    @POST("xcjh/admin/admin-base/upload-video")
    suspend fun uploadVideoOss(@Part file: MultipartBody.Part): ApiResponse<Any>



    /**
     * 获取首页广告列表
     *
     */
    @GET( "app/advertising/banner/list")
    suspend fun getBannerList(): ApiResponse<ArrayList<AdvertisementBanner>>


    /**
     * 获取首页热门比赛列表
     */
    @POST("app/schedule/latestMatch")
    suspend fun getOngoingMatch(@Body req: HotReq): ApiResponse<ArrayList<MatchBean>>
    /**
     *赛程热门赛事列表查询
     */
    @POST("app/schedule/getHotMatchList")
    suspend fun getHotMatch(@Body req: HotMatchReq): ApiResponse<ArrayList<HotMatchBean>?>
    /**
     *消除所有红点提示
     */
    @PUT("app/msg/cancelAllUnread")
    suspend fun getClreaAllMsg(): ApiResponse<*>
    /**
     *消除红点提示,读取该来源用户所有消息
     */
    @PUT("app/msg/cancelUnread")
    suspend fun getClreaMsg(@Body req: PostClreaMsgBean): ApiResponse<*>
    /**
     *赛程比赛分页查询
     */
    @POST("app/schedule/page")
    suspend fun getHotMatchChildList(@Body req: PostSchMatchListBean): ApiResponse<MyListPages<MatchBean>?>

    /**
     * 获取首页正在直播
     */
    @POST("app/home/living/page")
    suspend fun getNowLive(@Body req: LiveReq): ApiResponse<MyListPages<BeingLiveBean>>
    /**
     * 我关注的比赛分页查询
     */
    @POST("app/follow/matchPage")
    suspend fun getMyNoticeList(@Body req: BasePage): ApiResponse<MyListPages<MatchBean>>
    /**
     * 反馈通知页面：分页查询
     */
    @POST("app/msg/feedbackPage")
    suspend fun getFeedNoticeList(@Body req: BasePage): ApiResponse<MyListPages<FeedBackBean>>
    /**
     * 我的好友分页查询
     */
    @POST("app/follow/myFriendsPage")
    suspend fun getFriendsList(@Body req: BasePage): ApiResponse<MyListPages<FriendListBean>>
    /**
     * 消息列表页面：消息分页查询 返回对象fromId=0代表这条数据为反馈通知
     */
    @POST("app/msg/msgPage")
    suspend fun getMsgListPage(@Body req: PostGetMsgBean): ApiResponse<MyListPages<MsgListBean>>
    /**
     * 上传聊天照片
     */
    @Multipart
    @POST("app/file/chat/image")
    suspend fun upLoadChatPic(@Part file: MultipartBody.Part): ApiResponse<String>
    /**
     * APP用户登录注册
     */
    @POST("app/common/login")
    suspend fun getLogin(@Body req: PostLoaginBean): ApiResponse<String>
    /**
     * 获取验证码
     */
    @GET("app/common/getKaptchaImage")
    suspend fun getTypeCode(@Body req: PostLoaginBean): ApiResponse<String>
    /**
     * 关注比赛
     */
    @POST("app/follow/match/follow/{matchId}/{matchType}")
    suspend fun getNoticeRaise(
        @Path("matchId") matchId: String,
        @Path("matchType") matchType: String
    ): ApiResponse<*>
    /**
     * 关注主播
     */
    @POST("app/follow/anchor/follow/{anchorId}")
    suspend fun getNoticeUser(@Path("anchorId") anchorId: String
    ): ApiResponse<*>

    /**
     * 取消关注比赛
     */
    @POST("app/follow/match/unfollow/{matchId}/{matchType}")
    suspend fun getUnNoticeRaise( @Path("matchId") matchId: String,
                                   @Path("matchType") matchType: String): ApiResponse<*>
    /**
     * 取消好友
     */
    @POST("app/follow/anchor/unfriend/{anchorId}")
    suspend fun getUnNoticeFriend( @Path("anchorId") matchId: String): ApiResponse<*>

    /**
     * 根据主播ID，删除聊天记录
     * /apis/app/msg/delRecordsByAnchorId
     */
    @PUT( "app/msg/delRecordsByAnchorId")
    suspend fun delMsgByid(@Body req: DelMsgBean):  ApiResponse<*>

    /**
     * 获取首页新闻列表
     */
    @POST("app/news/page")
    suspend fun getNewsList(@Body req: NewsReq): ApiResponse<MyListPages<NewsBean>>


    /**
     * 获取新闻详情
     */
    @GET("app/news/getNewsInfo/{id}")
    suspend fun getNewsInfo(@Path("id") id: String): ApiResponse<NewsBean>

    /**
     * 获取搜索正在进行的热门比赛标签    MatchBean
     */
    @POST("app/schedule/ongoingMatch")
    suspend fun getHotOngoingMatch(@Body req: HotReq): ApiResponse<ArrayList<BeingLiveBean>>


    /**
     * 获取个人中心活动列表
     */
    @POST("app/activity/page")
    suspend fun getEventsList(@Body req: ListReq): ApiResponse<MyListPages<EventsBean>>


    /**
     * 获取活动中心详情
     */
    @GET("app/activity/getActivityInfo/{id}")
    suspend fun getActivityInfo(@Path("id") id: String): ApiResponse<EventsBean>


    /**
     * 获取我关注的主播列表
     */
    @POST("app/follow/anchorPage")
    suspend fun getAnchorPageList(@Body req: ListReq): ApiResponse<MyListPages<FollowAnchorBean>>


    /**
     * 获取个人中心历史观看记录
     */
    @POST("app/user/live/history/page/list")
    suspend fun getHistoryLive(@Body req: ListReq): ApiResponse<MyListPages<BeingLiveBean>>

    /**
     * 消息列表页面：历史消息查询 复用型接口
     */
    @POST("app/msg/history")
    suspend fun getHistoryMsg(@Body req: HistoryMsgReq): ApiResponse<MutableList<MsgBean>>

    /**
     * 获取用户基本信息
     * /app/user/getUserInfo
     */
    @GET( "app/user/getUserInfo")
    suspend fun getUserBaseInfo():  ApiResponse<UserInfo>

    /**
     * 修改个人信息
     * /apis/app/user/updateInfo
     */
    @PUT( "app/user/updateInfo")
    suspend fun updateInfo(@Body req: InfoReq):  ApiResponse<*>

    /**
     * 上传头像照片
     *app/file/user/icon
     */
    @Multipart
    @POST("app/file/user/icon")
    suspend fun upLoadPic(@Part file: MultipartBody.Part): ApiResponse<String>

    /**
     * 上传普通图片
     *app/file/user/icon
     */
    @Multipart
    @POST("app/file/common/image")
    suspend fun upLoadImage(@Part file: MultipartBody.Part): ApiResponse<String>
    /**
     * 直播详情界面中主播详情接口
     */
    @GET("app/live/detail/live/user/info/{userId}")
    suspend fun getDetailAnchorInfo(@Path("userId") userId: String): ApiResponse<DetailAnchorBean>

    /**
     * 直播详情界面中指数接口
     */
    @GET("app/live/detail/football/odds/{matchId}")
    suspend fun getOddsInfo(@Path("matchId") matchId: String): ApiResponse<OddsBean>

    /**
     * 用户提交反馈
     */
    @POST("app/feedback/submit")
    suspend fun submitFeedback(@Body req: FeedbackReq): ApiResponse<*>

    /**
     * 直播详情界面中跑马灯接口
     */
    @GET("app/advertising/text/scroll/list")
    suspend fun getScrollTextList(): ApiResponse<ArrayList<ScrollTextBean>>

    /**
     * 查询直播间广告
     */
    @GET("app/advertising/live/show")
    suspend fun getShowAd(): ApiResponse<ScrollTextBean>

    /**
     * 查询文字直播信息-足球
     */
    @GET("app/live/detail/football/text/live/{matchId}")
    suspend fun getLiveEvent(@Path("matchId") matchId: String): ApiResponse<ArrayList<LiveTextBean>>

    /**
     * 查询重要事件-足球
     */
    @GET("app/live/detail/football/incidents/{matchId}")
    suspend fun getIncidents(@Path("matchId") matchId: String): ApiResponse<ArrayList<IncidentsBean>>

    /**
     * 取消关注主播
     */
    @POST("app/follow/anchor/unfollow/{anchorId}")
    suspend fun unfollowAnchor(@Path("anchorId") anchorId: String): ApiResponse<*>

    /**
     * 查询比赛基本信息
     * matchType比赛类型：1：足球；2：篮球,可用值:1,2
     */
    @POST("app/schedule/match/{matchId}/{matchType}")
    suspend fun getMatchDetail(@Path("matchId") matchId: String, @Path("matchType") matchType: String?): ApiResponse<MatchDetailBean>

    /**
     * 获取是否更新
     * 	渠道：1：安卓 2：IOS
     */
    @POST("app/version/getLatestVersion/{channel}")
    suspend fun getLatestVersion(@Path("channel") channel: String="1"): ApiResponse<AppUpdateBean>

    /**
     * 个人中心广告
     */
    @GET("app/advertising/personal/center")
    suspend fun getIndividualCenter(): ApiResponse<AdvertisementBanner>

    /**
     * 查询比赛阵容-足球
     */
    @GET("app/live/detail/football/lineup/{matchId}")
    suspend fun getFootballLineUp(@Path("matchId") matchId: String): ApiResponse<FootballLineupBean>

    /**
     * 查询比赛阵容-足球
     */
    @GET("app/live/detail/basketball/match/lineup/{matchId}")
    suspend fun getBasketballLineUp(@Path("matchId") matchId: String): ApiResponse<BasketballLineupBean>

    /**
     * 查询比赛得分-篮球
     */
    @GET("app/live/detail/basketball/match/score/{matchId}")
    suspend fun getBasketballScore(@Path("matchId") matchId: String): ApiResponse<BasketballScoreBean>

    /**
     * 查询比赛技术统计-篮球
     */
    @GET("app/live/detail/basketball/match/stats/{matchId}")
    suspend fun getBasketballStatus(@Path("matchId") matchId: String): ApiResponse<BasketballSBean>

    /**
     * 查询比赛技术统计-足球
     */
    @GET("app/live/detail/football/stats/{matchId}")
    suspend fun getFootballStatus(@Path("matchId") matchId: String): ApiResponse<ArrayList<StatusBean>?>

    /**
     * 新增观看直播历史
     */
    @GET("app/user/live/history/add/{liveId}")
    suspend fun addLiveHistory(@Path("liveId") liveId: String?): ApiResponse<*>


    /**
     * APP退出
     */
    @DELETE( "app/user/logout")
    suspend fun exitLogin(): ApiResponse<*>
}