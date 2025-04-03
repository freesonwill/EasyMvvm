package com.walisport.lib.base.utils

import android.view.LayoutInflater
import java.lang.reflect.Method

/**
 * @author: zhangsan
 * @date: 2025/3/14 11:37
 * @description:
 */
object CommonUtils {

    /**
     * 判断是否为空 并传入相关操作
     */
    inline fun <reified T> T?.notNull(notNullAction: (T) -> Unit, nullAction: () -> Unit = {}) {
        if (this != null) {
            notNullAction.invoke(this)
        } else {
            nullAction.invoke()
        }
    }

    val <T> Class<T>.inflateMethod: Method?
        get() =
            try {
                getMethod("inflate", LayoutInflater::class.java)
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
                null
            } catch (e: SecurityException) {
                e.printStackTrace()
                null
            }

}
