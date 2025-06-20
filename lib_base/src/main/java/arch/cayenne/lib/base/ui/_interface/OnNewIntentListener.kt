package arch.cayenne.lib.base.ui._interface

import android.content.Intent

/**
 * @date: 2025/6/20 10:35
 * @description: 启动相同Activity/Fragment时触发
 */
interface OnNewIntentListener {
    fun onNewIntent(intent: Intent)
}