package com.xcjh.base_lib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.xcjh.base_lib.appContext


/**
 * 泛型的高级特性 泛型实例化
 * 跳转
 */
inline fun <reified T> startNewActivity( block: Intent.() -> Unit = {}) {
    val intent = Intent(appContext, T::class.java)
    //把intent实例 传入block 函数类型参数
    intent.block()
    if (appContext.applicationContext !is Activity) { //不在activity作用域内跳转要加FLAG_ACTIVITY_NEW_TASK标记
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    appContext.startActivity(intent)
}

/**
 * arr_string 转 MutableList
 */
inline fun <reified T> jsonToList(jsonStr: String) : MutableList<T> {
    val jsonArray = JsonParser().parse(jsonStr).asJsonArray
    val list: MutableList<T> = ArrayList()
    for (jsonElement in jsonArray) {
        list.add(Gson().fromJson(jsonElement, T::class.java)) //cls
    }
    return list
}

/**
 * gson to obj
 */
inline fun <reified T> jsonToObject(jsonStr: String?) : T {
    return Gson().fromJson(jsonStr, T::class.java)
}

/**
 * string 转 map
 */
inline fun <reified T> string2map(str_json: String?): Map<String?, T?>? {
    var res: Map<String?, T?>? = null
    try {
        val gson = Gson()
        res = gson.fromJson(str_json, object : TypeToken<Map<String?, T?>?>() {}.type)
    } catch (e: JsonSyntaxException) {
    }
    return res
}

inline fun <reified T> startService(context: Context, block: Intent.() -> Unit) {
    val intent = Intent(context, T::class.java)
    //把intent实例 传入block 函数类型参数
    intent.block()
    context.startService(intent)
}
