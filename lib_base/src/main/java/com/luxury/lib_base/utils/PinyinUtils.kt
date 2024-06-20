package com.luxury.lib_base.utils


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:42
 **/
object PinyinUtils {
    private val dic by lazy {
        mapOf(
            0 to "ling",1 to "yi", 2 to "er", 3 to "san", 4 to "si",5 to "wu", 6 to "liu",
            10 to "shi", 50 to "wushi", 100 to "yibai",
            200 to "liangbai", 500 to "wubai", 1000 to "qian",
            2000 to "liangqian", 5000 to "wuqian", 10000 to "yiwan",
            20000 to "liangwan", 50000 to "wuwan", 100000 to "shiwan",
        )
    }

    fun toPinyin(n: Int): String {
        return dic[n] ?: throw IllegalArgumentException("pinyin dic not contain $n")
    }
}