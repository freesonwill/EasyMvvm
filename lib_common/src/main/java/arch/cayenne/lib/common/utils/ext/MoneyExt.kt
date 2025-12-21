package arch.cayenne.lib.common.utils.ext

/**
 *
 * @date: 2025/12/19 15:21
 * @description:
 */

fun String.ccyToSymbol(): String {
    return when (this) {
        "USD" -> "$"
        "CNY" -> "¥"
        "JPY" -> "¥" // 日元
        "KRW" -> "₩" // 韩元
        "EUR" -> "€" // 欧元
        "GBP" -> "£" // 英镑
        "HKD" -> "HK$" // 港元
        "SGD" -> "S$" // 新加坡元
        "AUD" -> "A$" // 澳元
        "CAD" -> "C$" // 加元
        "THB" -> "฿" // 泰铢
        "VND" -> "₫" // 越南盾
        "IDR" -> "Rp" // 印尼盾
        "PHP" -> "₱" // 菲律宾比索
        "MYR" -> "RM" // 马来西亚林吉特
        "PKR" -> "₨" // 巴基斯坦卢比
        "LKR" -> "₨" // 斯里兰卡卢比
        "CHF" -> "CHF" // 瑞士法郎
        "NZD" -> "NZ$" // 新西兰元
        "SEK" -> "kr" // 瑞典克朗
        "NOK" -> "kr" // 挪威克朗
        "DKK" -> "kr" // 丹麦克朗
        "ZAR" -> "R" // 南非兰特
        "MXN" -> "$" // 墨西哥比索
        "RUB" -> "₽" // 俄罗斯卢布
        "BRL" -> "R$" // 巴西雷亚尔
        "INR" -> "₹" // 印度卢比
        "TRY" -> "₺" // 土耳其里拉
        "AED" -> "د.إ" // 阿联酋迪拉姆
        "SAR" -> "ر.س" // 沙特里亚尔
        "ILS" -> "₪" // 以色列新谢克尔
        "EGP" -> "£" // 埃及镑
        "NGN" -> "₦" // 尼日利亚奈拉
        "KES" -> "KSh" // 肯尼亚先令
        "GHS" -> "₵" // 加纳塞地
        "TZS" -> "TSh" // 坦桑尼亚先令
        "UGX" -> "USh" // 乌干达先令
        "ZMW" -> "ZK" // 赞比亚克瓦查
        "ZWL" -> "Z$" // 津巴布韦元
        else -> "$"
    }


}

fun String.symbolUrl(): String {
    return when (this) {
        "CNY" -> "https://dev.ra781.com/gameresource/ccy/cny.png"
        "USD" -> "https://dev.ra781.com/gameresource/ccy/usd.png"
        "USDT" -> "https://dev.ra781.com/gameresource/ccy/usdt.png"
        "EUR" -> "https://dev.ra781.com/gameresource/ccy/eur.png"
        "BTC" -> "https://dev.ra781.com/gameresource/ccy/btc.png"
        "ETH" -> "https://dev.ra781.com/gameresource/ccy/eth.png"
        else -> "https://dev.ra781.com/gameresource/ccy/usd.png"
    }

}