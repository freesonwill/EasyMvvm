package arch.cayenne.lib.http.data

data class HttpApiResponse<T>(
    val code: Int,
    val message: String,
    val timestamp: Long,
    val data: T
)

const val TOKEN_INVALID = -201 //当前连接已过期，请重新尝试登录

const val ACCOUNT_INVALID = 1000//账号不存在, 请退出重新登录游戏