package com.xcjh.base_lib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.xcjh.base_lib.base.container.ContainerFm2Activity
import com.xcjh.base_lib.base.container.ContainerFmActivity

class Constants {

    companion object {
        /**
         * 登录过期
         */
        const val EXPIRED_CODE = 4005
        const val NEEDLOAGIN_CODE = 401
        const val APP_ID = "8888"
        const val BASE_PAGE_SIZE = 20
        //微信
        const val WX_APPID = "wx5568f9de502defe3"
        const val WX_APPSecret = "35758f387da2633589629f33fd90cc2c"

        //调整参数
        const val CHAT_TITLE = "CHAT_TITLE"//用户ID
        const val USER_NICK = "USER_NICK"//主播昵称
        const val USER_ID = "USER_ID"//用户ID
        const val USER_HEAD = "USER_HEAD"//主播头像
        const val CHAT_DATA = "CHAT_DATA"//数据
        const val WEB_URL = "WEB_URL"//网站地址
         var PHONE_CODE = ""//手机区号
        const val VIDEO_DATA = "VIDEO_DATA"//视频详情
        const val BUNDLE_DATA = "BUNDLE_DATA"//详情数据
        const val TOURNAMENT_DATA = "TOURNAMENT_DATA"//锦标赛详情
        const val FIND_SEARCH_PAGE = "FIND_SEARCH_PAGE"//发现跳转页面

        const val MATCH_DETAIL_NAME = ""//比赛详情名称
        const val MATCH_DETAIL_MODE = "MATCH_DETAIL_MODE"//比赛详情纯净模式还是普通模式
        const val CHAT_PURE_MODE = "mode"//比赛详情聊天tab纯净模式还是普通模式
        const val CHAT_ID = "chat_id"//聊天室id
        const val ANCHOR_ID = "anchor_id"//主播id
        const val MATCH_TYPE = "type"//足球比赛还是篮球比赛
        const val WEB_VIEW_TYPE = "type"//是加载普通url或者是一段html
        const val WEB_VIEW_ID= "WEB_VIEW_ID"//获取url详情id
        //test
//        const val RewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"//激励视频
//        const val NativeAdUnitId = "ca-app-pub-3940256099942544/2247696110"//原生广告
//        const val LaunchAdUnitId = "ca-app-pub-3940256099942544/3419835294"//开屏广告
//        const val BannerAdUnitId = "ca-app-pub-3940256099942544/6300978111"//条幅广告
//        const val InterstitialAdUnitId = "ca-app-pub-3940256099942544/1033173712"//插屏广告
        //正式
        const val RewardedAdUnitId = "ca-app-pub-5931382258102592/8671431977"//激励视频
        const val NativeAdUnitId = "ca-app-pub-5931382258102592/3902321026"//原生广告
        const val LaunchAdUnitId = "ca-app-pub-5931382258102592/8458280183"//开屏广告
        const val BannerAdUnitId = "ca-app-pub-5931382258102592/8753467658"//条幅广告
        const val InterstitialAdUnitId = "ca-app-pub-5931382258102592/9978919416"//插屏广告

        /**
         * 跳转容器页面
         * @param routePath Fragment路由地址
         * @param bundle        跳转所携带的信息
         */
        fun startContainerActivity(
            context: Context,
            routePath: String?,
            bundle: Bundle? = null,
            reqCode: Int? = null
        ) {
            val intent = Intent(context, ContainerFmActivity::class.java)
            intent.putExtra(ContainerFmActivity.FRAGMENT, routePath)
            if (bundle != null) intent.putExtra(ContainerFmActivity.BUNDLE, bundle)
            if (reqCode == null)
                context.startActivity(intent)
            else
                (context as Activity).startActivityForResult(intent, reqCode)
        }

        /**
         * /直接传fragment
         * /不通过路由
         */

        fun startContainer2Activity(
            context: Context,
            fragment: Fragment,
            bundle: Bundle? = null,
            reqCode: Int? = null
        ) {
            ContainerFm2Activity().startContainer2Activity(context, fragment, bundle, reqCode)
        }
    }

    /**
     * value规则： /(module后缀)/(所在类名)
     * 路由 A_ : Activity
     *     F_ : Fragment
     */
    interface Router {

        //广告
        object Ad {
            const val A_Ad_MAIN = "/ad/MainActivity"
        }

        //事件统计
        object Analytic {
            const val A_Analytic_MAIN = "/analytic/MainActivity"
        }

        //登录
        object Login {
            const val A_Login_MAIN = "/login/MainActivity"
        }

        //推送
        object Push {
            const val A_Push_MAIN = "/push/MainActivity"
        }

        //排序
        object Ranking {
            const val A_Ranking_MAIN = "/ranking/MainActivity"
        }

        //充值
        object Recharge {
            const val A_Recharge_MAIN = "/recharge/MainActivity"
        }

        //分享
        object Share {
            const val A_Share_MAIN = "/share/MainActivity"
        }

        //短信通道
        object Sms {
            const val A_Sms_MAIN = "/sms/MainActivity"
        }

        object Community {

            //我的好友
            const val A_COMMUNITY_MY_FOCUS_FANS = "/community/my_focus_fans"

            //文章 公告详情
            const val A_COMMUNITY_ARTICLE_DETAIL = "/community/article_detail"

            //社区收米号主页
            const val F_COMMUNITY_MAIN = "/community/main"

            //收米号主页
            const val F_COMMUNITY_HOME_SHULAN = "/community/home_shulan"

            //社区主页
            const val F_COMMUNITY_HOME_COMMUNITY = "/community/home_community"

            //赛程赛事 筛选
            const val F_COMMUNITY_SCHEDULE_FILTER = "/community/schedule_filter"

            //热门话题
            const val F_COMMUNITY_HOT_THEME = "/community/hot_theme"

            //我的粉丝
            const val F_COMMUNITY_MY_FANS = "/community/my_fans"

            //文章列表
            const val F_COMMUNITY_ARTICLE_LIST = "/community/article"

            //专区列表
            const val F_COMMUNITY_SCHEME_PLAY = "/community/scheme_play"


        }


    }

}