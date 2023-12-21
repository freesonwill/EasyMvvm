/*
package com.xcjh.app.event

import com.google.firebase.analytics.FirebaseAnalytics
import kotlin.jvm.JvmOverloads
import android.os.Bundle
import com.xcjh.base_lib.appContext
import kotlin.jvm.Volatile

*/
/**
 * @author zobo
 *//*

class DataReportUtils {
    init {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(appContext)
    }
    */
/**
     * 埋点
     *
     * @param key
     * @param value
     *//*

    @JvmOverloads
    fun report(key: String, value: Bundle? = null) {
        mFirebaseAnalytics.logEvent(key, value)
    }

    companion object {
        @Volatile
        var instance: DataReportUtils? = null
            get() {
                if (field == null) {
                    field = DataReportUtils()
                }
                return field
            }
            private set
        private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    }
}*/
