package arch.cayenne.lib.skin.util

import android.content.Context
import androidx.collection.LruCache

object ResUtils {
   private val _suffixResourceCache = LruCache<String,Int>(150)
   private val _originResourceCache = LruCache<String,Int>(150)

    /**
     * 获取带后缀名的资源Id
     * */
    fun getSuffixResourceId(context: Context, resourceName: String, defType: String): Int {
        val key = resourceName + "_" + defType
        val cachedId = _suffixResourceCache[key]
        if (cachedId != null) {
            return cachedId
        }
        val resId = context.resources.getIdentifier(resourceName, defType, context.packageName)
        _suffixResourceCache.put(key, resId)
        return resId
    }

    /**
     * 获取不带后缀名的资源Id
     * */
    fun getOriginalResourceId(context: Context,resourceName: String,defType: String):Int{
        val key = resourceName + "_" + defType
        val cachedId = _originResourceCache[key]
        if (cachedId != null) {
            return cachedId
        }
        val resId = context.resources.getIdentifier(resourceName, defType, context.packageName)
        _originResourceCache.put(key, resId)
        return resId
    }


}