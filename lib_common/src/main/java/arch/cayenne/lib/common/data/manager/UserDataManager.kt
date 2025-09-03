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

    inline fun <reified T> getValue(
        key: UserDataKey,
        default: T? = null,
        noinline deserializer: (String?) -> T? = { GsonUtils.fromJson(it, T::class.java) }
    ): T {
        return when (default) {
            is String -> mmkv.decodeString(key.key, default) as T
            is Boolean -> mmkv.decodeBool(key.key, default) as T
            is Int -> mmkv.decodeInt(key.key, default) as T
            is Long -> mmkv.decodeLong(key.key, default) as T
            is Float -> mmkv.decodeFloat(key.key, default) as T
            else -> deserializer(mmkv.decodeString(key.key)) as T
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
}