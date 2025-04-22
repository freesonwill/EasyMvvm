package arch.cayenne.lib.common.data

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class UserDataManager {

    private val mmkv = MMKV.defaultMMKV()
    private val flows = mutableMapOf<UserDataKey, MutableSharedFlow<Any?>>()

    fun <T> setKeyValue(key: UserDataKey, value: T) {
        when (value) {
            is String -> {
                mmkv.putString(key.key, value)
            }

            is Boolean -> {
                mmkv.putBoolean(key.key, value)
            }

            is Int -> {
                mmkv.putInt(key.key, value)
            }

            is Long -> {
                mmkv.putLong(key.key, value)
            }

            is Float -> {
                mmkv.putFloat(key.key, value)
            }
        }
        notifyChanged(key, value)
    }

    fun getStringValue(key: UserDataKey, default: String): String {
        return mmkv.getString(key.key, default) ?: default
    }

    fun getIntValue(key: UserDataKey, default: Int): Int {
        return mmkv.getInt(key.key, default)
    }

    private fun notifyChanged(key: UserDataKey, value: Any?) {
        val flow = flows.getOrPut(key) { MutableSharedFlow(replay = 1) }
        flow.tryEmit(value)
    }

    fun observe(key: UserDataKey): Flow<Any?> {
        val flow = flows.getOrPut(key) { MutableSharedFlow(replay = 1) }
        return flow
    }
}