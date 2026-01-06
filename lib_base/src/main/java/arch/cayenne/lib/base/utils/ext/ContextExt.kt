package arch.cayenne.lib.base.utils.ext

import android.content.Context

/**
 * @date: 2026/1/7 02:08
 * @description:
 */
fun Context.getActivity(): android.app.Activity? {
    var context = this
    while (context is android.content.ContextWrapper) {
        if (context is android.app.Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}