package arch.cayenne.lib.common.data.manager

import arch.cayenne.lib.common.data.constants.UserDataKey
import com.blankj.utilcode.util.GsonUtils
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class UserDataManager {
    val mmkv = MMKV.defaultMMKV()
    private val flows = mutableMapOf<UserDataKey, MutableSharedFlow<Any?>>()
    private fun getFlow(key: UserDataKey) = flows.getOrPut(key) { MutableSharedFlow(replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST) }

    fun <T> setKeyValue(
        key: UserDataKey,
        value: T?,
        serializer:(str:T?)->String? = { GsonUtils.toJson(it) }
    ) {
        when (value) {
            is String -> mmkv.putString(key.key, value)
            is Boolean -> mmkv.putBoolean(key.key, value)
            is Int -> mmkv.putInt(key.key, value)
            is Long -> mmkv.putLong(key.key, value)
            is Float -> mmkv.putFloat(key.key, value)
            else -> mmkv.putString(key.key,serializer(value))
        }
        notifyChanged(key, value)
    }

    fun removeValueForKey(key:UserDataKey){
        mmkv.removeValueForKey(key.key)
        flows.remove(key)
    }

    inline fun <reified T> getValue(key: UserDataKey, default: T): T {
        return when (default) {
            is String -> mmkv.getString(key.key, default)
            is Boolean -> mmkv.getBoolean(key.key, default)
            is Int -> mmkv.getInt(key.key, default)
            is Long -> mmkv.getLong(key.key, default)
            is Float -> mmkv.getFloat(key.key, default)
            else -> throw IllegalStateException("❌ Unsupported type: ${T::class}")
        } as T
    }

    inline fun <reified T> getValue(key: UserDataKey): T? {
        return when (T::class) {
            String::class -> getValue(key,"") as T
            Boolean::class -> getValue(key,false) as T
            Int::class -> getValue(key,0) as T
            Long::class -> getValue(key,0) as T
            Float::class -> getValue(key,0) as T
            else -> {
                val json = mmkv.decodeString(key.key)
                (if (json.isNullOrEmpty()) null else GsonUtils.fromJson(json, T::class.java))
            }
        }
    }

    private fun notifyChanged(key: UserDataKey, value: Any?) {
        val flow = getFlow(key)
        val b = flow.tryEmit(value)
        if(!b) throw IllegalStateException("❌ notifyChanged--tryEmit failed for key: $key, value: $value")
    }

    fun <T> observe(key: UserDataKey): Flow<T> {
        val flow = getFlow(key)
        @Suppress("UNCHECKED_CAST")
        return flow as Flow<T>
    }

    /**
     * 通知令牌的变化。
     *
     * 此方法获取与用户令牌（KEY_TOKEN）相关联的流，并尝试向该流发射当前存储的令牌值。
     * 如果发射失败，将不会抛出异常。
     */
    fun notifyToken() {
        val flow = getFlow(UserDataKey.KEY_TOKEN)
        flow.tryEmit(getValue(UserDataKey.KEY_TOKEN, ""))
    }
}