package com.walisport.module.live.helper

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.manager.UserDataManager
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import com.walisport.module.live.utils.SoftKeyBoardHeightListener
import org.koin.java.KoinJavaComponent.inject

/**
 * @author: wenxi
 * @date: 14/8/25 11:12
 * @description: 在activity中监听软件盘高度 并且存储软件盘高度到本地如果没有获取到就
 */
class SoftKeyboardHeightHelper() {
    private var softKeyboardHeightListener: SoftKeyBoardHeightListener? = null
    private val userManager: UserDataManager by inject(UserDataManager::class.java)
    private val TAG = SoftKeyboardHeightHelper::class.java.simpleName

    fun startListener(activity: Activity) {
        softKeyboardHeightListener = SoftKeyBoardHeightListener(activity)
        softKeyboardHeightListener?.registerKeyboardHeightListener(object :
            SoftKeyBoardHeightListener.KeyboardHeightListener {
            override fun onKeyboardHeightChanged(height: Int) {
                "height change $height".logd("aaa")
                if (height > 150.dp2px) {
                }
            }
        })
        softKeyboardHeightListener?.start()
        getKeyboardHeightByReflection(activity)
    }


    fun getKeyboardHeightByReflection(activity: Activity): Int {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        // 获取可用屏幕高度
        val usableMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(usableMetrics)
        return usableMetrics.heightPixels
    }


    fun close() {
        softKeyboardHeightListener?.close()
        softKeyboardHeightListener = null
    }

}