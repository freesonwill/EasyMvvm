package com.walisport.lib.common.utils

import android.content.res.Resources
import android.util.TypedValue

object ViewUtils {
    fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        )
    }
}