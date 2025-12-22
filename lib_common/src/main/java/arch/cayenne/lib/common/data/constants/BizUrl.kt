package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.base.BuildConfig

/**
 *
 * @date: 2025/12/10 16:44
 * @description:
 */

enum class BizUrl(val url: String) {
    FUND_DETAIL("${BASE_URL}/web-3n1/record"),
    FEEDBACK("${BASE_URL}/web-3n1/feedback"),
    //GAME_BET_SHARE("http://192.168.10.38:5173/orderDetail?settleId=s3&name=南京红姐&level=90"),
    GAME_BET_SHARE("${BASE_URL}/web-3n1/orderDetail?settleId=s3&name=南京红姐&level=90"),
    REBATE("${BASE_URL}/web-3n1/rebate"),
    //INVITE("http://192.168.10.38:5173/invite") ,
    INVITE("${BASE_URL}/web-3n1/invite"),
    PARTNER("${BASE_URL}/web-3n1/partner"),
    VIP("${BASE_URL}/web-3n1/vip"),
    HELP("${BASE_URL}/web-help/zh-CN/"), //帮助页面
    TOP_LESSON("${BASE_URL}/web-help/zh-CN/wallet/tutorial/?backUrl=/web-help/zh-CN/wallet/tutorial/"), //充值教程
    WITHDRAW_LESSON("${BASE_URL}/web-help/zh-CN/wallet/withdraw/?backUrl=/web-help/zh-CN/wallet/withdraw/"), //提现教程
    ACTIVITY("${BASE_URL}/web-3n1/activity"), //活动页面
}

val BASE_URL
    get() = run {
        when (BuildConfig.BUILD_TYPE) {
            "qatest" -> {
                "https://test.ra781.com"
            }

            "release" -> {
                "https://test.ra781.com"
            }

            else -> {
                "https://dev.ra781.com"
                //"http://192.168.10.38:5173"
            }
        }
    }


val SPORT_SERVER_WSS
    get() = run {
        when (BuildConfig.BUILD_TYPE) {
            "qatest" -> {
                "wss://sport-dev.ra781.com/api/sport/ws"
            }

            "release" -> {
                "wss://sport-dev.ra781.com/api/sport/ws"
            }

            else -> {
                "wss://sport-dev.ra781.com/api/sport/ws"
            }
        }
    }


val CHAT_SERVER
    get() = run {
        when (BuildConfig.BUILD_TYPE) {
            "qatest" -> {
                "wss://sport-dev.ra781.com/api/game/chat/ws"
            }

            "release" -> {
                "wss://sport-dev.ra781.com/api/game/chat/ws"
            }

            else -> {
                "wss://sport-dev.ra781.com/api/game/chat/ws"
            }
        }
    }



