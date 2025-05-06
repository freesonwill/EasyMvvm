package arch.cayenne.lib.common.data.manager

import arch.cayenne.lib.common.data.constants.UserDataKey
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class UserDataManager {
    val mmkv = MMKV.defaultMMKV()
    private val flows = mutableMapOf<UserDataKey, MutableSharedFlow<Any?>>()

    fun <T> setKeyValue(key: UserDataKey, value: T) {
        when (value) {
            is String -> mmkv.putString(key.key, value)
            is Boolean -> mmkv.putBoolean(key.key, value)
            is Int -> mmkv.putInt(key.key, value)
            is Long -> mmkv.putLong(key.key, value)
            is Float -> mmkv.putFloat(key.key, value)
            else -> throw IllegalArgumentException("❌ Unsupported type: ${value!!::class}")
        }
        notifyChanged(key, value)
    }

    inline fun < reified T> getValue(key: UserDataKey, default: T): T {
        return when (default) {
            is String -> mmkv.getString(key.key, default)
            is Boolean -> mmkv.getBoolean(key.key, default)
            is Int -> mmkv.getInt(key.key, default)
            is Long -> mmkv.getLong(key.key, default)
            is Float -> mmkv.getFloat(key.key, default)
            else -> throw IllegalStateException("❌ Unsupported type: ${T::class}")
        } as T
    }

    private fun notifyChanged(key: UserDataKey, value: Any?) {
        val flow = flows.getOrPut(key) { MutableSharedFlow(replay = 1) }
        flow.tryEmit(value)
    }

    fun <T> observe(key: UserDataKey): Flow<T> {
        val flow = flows.getOrPut(key) { MutableSharedFlow(replay = 1) }
        @Suppress("UNCHECKED_CAST")
        return flow as Flow<T>
    }
}