package com.cn.game.sdk2.utils


/**
 * Description:
 * author       : zhangsan
 * createTime   : 2024/6/17 16:42
 **/
object PinyinUtils {
    private val dic by lazy {
        mapOf(
            0 to "ling",1 to "yi", 2 to "er", 3 to "san", 4 to "si",5 to "wu", 6 to "liu",
            1000 to "shi", 5000 to "wushi", 10000 to "yibai",
            20000 to "liangbai", 50000 to "wubai", 100000 to "qian",
            200000 to "liangqian", 500000 to "wuqian", 1000000 to "yiwan",
            2000000 to "liangwan", 5000000 to "wuwan", 10000000 to "shiwan",
        )
    }
//    200000))
//    noteList.add(SelectAnnotationBean(money = 500000))
//    noteList.add(SelectAnnotationBean(money = 1000000))
//    noteList.add(SelectAnnotationBean(money = 2000000))
//    noteList.add(SelectAnnotationBean(money = 5000000))
//    noteList.add(SelectAnnotationBean(money = 10000000))

    fun toPinyin(n: Int): String {
        return dic[n] ?: throw IllegalArgumentException("pinyin dic not contain $n")
    }
}