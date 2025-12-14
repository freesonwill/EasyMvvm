package arch.cayenne.lib.common.data.constants

import arch.cayenne.lib.base.BuildConfig

/**
 *
 * @date: 2025/12/10 16:44
 * @description:
 */
enum class BizUrl(val url: String) {
    FUND_DETAIL("${BASE_URL}/web-3n1/record") ,
    FEEDBACK("${BASE_URL}/web-3n1/feedback") ,

    //GAME_BET_SHARE("http://192.168.10.37:5173/orderDetail?settleId=s3&name=南京红姐&level=90")
    GAME_BET_SHARE("${BASE_URL}/web-3n1/orderDetail?settleId=s3&name=南京红姐&level=90") ,
    REBATE("${BASE_URL}/web-3n1/rebate") ,
    INVITE("${BASE_URL}/web-3n1/invite") ,
    PARTNER("${BASE_URL}/web-3n1/partner") ,
    VIP("${BASE_URL}/web-3n1/vip") ,
    HELP("https://www.ve657.com/help-docs/help/") ,//暂时用这个地址，Web端要重新做帮助
    TOP_LESSON("${BASE_URL}/web-3n1/help?url=/wallet/tutorial/") //充值教程


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
            }
        }
    }


