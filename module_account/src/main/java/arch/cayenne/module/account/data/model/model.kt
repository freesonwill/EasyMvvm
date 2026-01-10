package arch.cayenne.module.account.data.model

/**
 *
 * @date: 2026/1/9 11:15
 * @description:
 */
data class LoginVo(
    val countryCode: String, // 区号
    val nationalNumber: String, // 国家号码
    val pwd: String, // 密码
    val mobileType: String, // 设备型号
    val osVer: String, // 操作系统版本
    val jb: Boolean, // 是否越狱
    val sms: String? = null, // 手机验证码
    val clientId: String? = null, // 第三方账号平台账号
    val authSecret: String? = null // 第三方账号平台token
)


data class LoginResponseVo(
    val uid: Long, // 玩家ID，登录成功才会返回
    val status: Int, // 登录状态：0=成功，1=失败
    val isReg: Boolean, // 是否进入注册流程
    val needVerify: Boolean, // 是否需要验证
    val token: String, // 登录Token
    val reason: String // 登录失败信息
)
