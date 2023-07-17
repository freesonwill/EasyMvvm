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
