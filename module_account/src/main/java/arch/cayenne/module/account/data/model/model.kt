package arch.cayenne.module.account.data.model

/**
 *
 * @date: 2026/1/9 11:15
 * @description:
 */
/**
 * 用户登录/注册请求
 */
data class LoginVo(
    val countryCode: String, // 区号，例如: 86
    val nationalNumber: String, // 国家号码，例如: 18812345678
    val pwd: String, // 密码
    val mobileType: String, // 设备型号，例如: iPhone17,1
    val osVer: String, // 操作系统版本
    val jb: Boolean, // 是否越狱
    val sms: String, // 手机验证码
    val clientId: String, // 第三方账号平台账号
    val authSecret: String // 第三方账号平台token
)


data class LoginResponseVo(
    val uid: Long, // 玩家id, 登陆成功才会返回
    val status: Int, // 登陆状态。0=success,1=fail
    val needVerify: Boolean, // 需要验证。true=需要验证
    val token: String, // 登陆token
    val reason: String? = null, // 登陆失败信息
    val reg: Boolean // 是否注册
)
