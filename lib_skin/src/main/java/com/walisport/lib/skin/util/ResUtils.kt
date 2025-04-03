package com.walisport.lib.skin.util

import android.content.Context
import androidx.collection.LruCache

object ResUtils {
   private val sResourceCache = LruCache<String,Int>(100)

    fun getResourceId(context: Context, resourceName: String, defType: String): Int {
        val key = resourceName + "_" + defType
        val cachedId = sResourceCache[key]
        if (cachedId != null) {
            return cachedId
        }
        val resId = context.resources.getIdentifier(resourceName, defType, context.packageName)
        sResourceCache.put(key, resId)
        return resId
    }
}