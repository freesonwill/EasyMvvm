package com.walisport.module.live.utils.softkeyboard

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd

/**
 * @author: wenxi
 * @date: 29/8/25 23:26
 * @description:
 */
class NavigationBarHelper(
    private val rootView: View,
    private val navigationListener: NavigationListener
) {
    private var navigationBarHeight: Int = 0
    private var isNavigationBarVisible: Boolean = false
    private var isKeyBoardVisible: Boolean = false

    init {
        setupWindowInsetsListener()
    }

    /**
     * 设置WindowInsets监听器来实时检测导航栏状态
     */
    private fun setupWindowInsetsListener() {
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            // 检测导航栏状态
            checkNavigationBarState(insets)
            checkKeyBoardState(insets)
            insets
        }
        // 立即请求WindowInsets更新
        ViewCompat.requestApplyInsets(rootView)
    }

    /**
     * 检测导航栏状态
     */
    private fun checkNavigationBarState(insets: WindowInsetsCompat) {
        val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        // 导航栏可见性判断
        isNavigationBarVisible = navBars.bottom > 0
        navigationBarHeight = navBars.bottom
    }

    /**
     * 检测导航栏状态
     */
    private fun checkKeyBoardState(insets: WindowInsetsCompat) {
        val keyboardInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val height = keyboardInsets.bottom

        if (height > 150 && !isKeyBoardVisible) {
            isKeyBoardVisible = true
            val keyBoardHeight = if(isNavigationBarVisible) height - navigationBarHeight else height
            navigationListener.onSoftKeyBoardShow(keyBoardHeight)
        } else if (height < 150 && isKeyBoardVisible) {
            isKeyBoardVisible = false
            navigationListener.onSoftKeyBoardHide()
        }
    }

}