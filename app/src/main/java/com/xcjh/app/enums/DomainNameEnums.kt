package com.xcjh.app.enums

enum class DomainNameEnums(var type : String) {
    /**
     * 生产环境域名
     */
     DomainName("app.wyjxx.cn"),

    /**
     * 新域名
     */
    NewDomainName("app.gdhsbp.cn"),

    /**
     * 预发布
     */
    PreDomainName("app.cbd246.com"),

    /**
     * 测试服
     */
    TestDomainName("192.168.101.15")
}