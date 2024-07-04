package com.xcjh.app.bean

/**
 * @Author pang
 * @Date 2024年07月02日   时间：15:40
 */
data class HostBean(
    val app: MutableList<AppHost>?
)

data class AppHost(
    val domainUrl: String?,
    val shareUrl: String?

)