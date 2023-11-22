package com.xcjh.base_lib.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.xcjh.base_lib.appContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


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
 * 从序列化中检索到数据
 * @param name 键名
 * @param defValue 默认值
 */
inline fun <reified T> Activity?.bundle(defValue: T? = null, name: String? = null) =
    lazyField {
        val adjustName = name ?: it.name
        val result = when {
            Parcelable::class.java.isAssignableFrom(T::class.java) -> this?.intent?.getParcelableExtra<Parcelable>(
                adjustName
            ) as? T
            else -> this?.intent?.getSerializableExtra(adjustName) as? T
        }
        result ?: defValue ?: null as T
    }


/**
 * 从序列化中检索到数据
 * @param name 键名
 * @param defValue 默认值
 */
@JvmSynthetic
inline fun <reified T> Fragment?.bundle(defValue: T? = null, name: String? = null) =
    lazyField {
        val adjustName = name ?: it.name
        val result = when {
            Parcelable::class.java.isAssignableFrom(T::class.java) -> this?.arguments?.getParcelable<Parcelable>(
                adjustName
            ) as? T
            else -> this?.arguments?.getSerializable(adjustName) as? T
        }
        result ?: defValue ?: null as T
    }

/**
 * 延迟初始化
 * 线程安全
 * 等效于[lazy], 但是可以获取委托字段属性
 */
@Suppress("UNCHECKED_CAST")
fun <T, V> T.lazyField(block: T.(KProperty<*>) -> V) = object : ReadWriteProperty<T, V> {
    @Volatile
    private var value: V? = null
    override fun getValue(thisRef: T, property: KProperty<*>): V {

        return synchronized(this) {
            if (value == null) {
                value = block(thisRef, property)
                value as V
            } else value as V
        }
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        synchronized(this) {
            this.value = value
        }
    }
}
/**
 * arr_string 转 MutableList
 */
inline fun <reified T> jsonToList(jsonStr: String) : ArrayList<T> {
    val list= ArrayList<T>()
    try {
        val jsonArray = JsonParser().parse(jsonStr).asJsonArray
        for (jsonElement in jsonArray) {
            list.add(Gson().fromJson(jsonElement, T::class.java)) //cls
        }
    }catch (e: Exception) {

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
 * gson to obj
 *
 */
inline fun <reified T> jsonToObject2(jsonStr: String?) : T? {

    return try {
        val type = object : TypeToken<T>() {}.type
        Gson().fromJson(jsonStr, type)
    } catch (e: Exception) {
        null
    }
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
