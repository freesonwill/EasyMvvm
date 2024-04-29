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
        const val EXPIRED_CODE = 10
        /**
         * 登录过期
         */
        const val EXPIRED_CODE2 = 11
        //微信
        const val WX_APPID = "wx5568f9de502defe3"
        const val WX_APPSecret = "35758f387da2633589629f33fd90cc2c"

        //test
        const val RewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"//激励视频
        const val NativeAdUnitId = "ca-app-pub-3940256099942544/2247696110"//原生广告
        const val LaunchAdUnitId = "ca-app-pub-3940256099942544/3419835294"//开屏广告
        const val BannerAdUnitId = "ca-app-pub-5931382258102592/3266008730"//开屏广告

        /*   const val RewardedAdUnitId = "ca-app-pub-5931382258102592/4579090401"//激励视频
           const val NativeAdUnitId = "ca-app-pub-5931382258102592/2691842404"//原生广告
           const val LaunchAdUnitId = "ca-app-pub-5931382258102592/3301667757"//开屏广告
           const val BannerAdUnitId = "ca-app-pub-5931382258102592/3266008730"//条幅广告*/
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
            ContainerFm2Activity().startContainer2Activity(context, fragment,bundle,reqCode)
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

        //websocket消息
        object Ws {
            const val A_Ws_MAIN = "/ws/MainActivity"
            const val A_Ws_MAIN2 = "/ws/Main2Activity"
        }
        //websocket消息
        object Web {
            const val A_Web_MAIN = "/web/MainActivity"
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