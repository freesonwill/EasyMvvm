package com.walisport.module.live.utils.softkeyboard

import android.os.Handler
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import kotlinx.coroutines.MainScope

/**
 * @author: wenxi
 * @date: 29/8/25 23:26
 * @description:
 */
class NavigationBarHelper(
    private val rootView: View,
    private val lifecycle: Lifecycle,
    private var navigationListener: NavigationListener?
) : DefaultLifecycleObserver {
    private var navigationBarHeight: Int = 0
    private var isNavigationBarVisible: Boolean = false
    private var isKeyBoardVisible: Boolean = false
    private var dbKeyboardHeight: Int = 0
    //重启后进入时，首次检测的软件盘高度不对，弹出后又会更新正确软件盘高度
    private var secondCheck: Boolean = false
    private var lastKeyBoardHeight: Int = 0
    private val TAG = NavigationListener::class.java.simpleName


    init {
        setupWindowInsetsListener()
        lifecycle.addObserver(this)
    }

    fun setDbKeyBoardHeight(height: Int) {
        dbKeyboardHeight = height
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

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        lifecycle.removeObserver(this)
        //remove OnApplyWindowInsetsListener when destroyed,otherwise it will cause fragment memory leak
        ViewCompat.setOnApplyWindowInsetsListener(rootView, null)
        navigationListener = null
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

        if (secondCheck) { //保存高度和计算高度不一致时第二次检查
            secondCheck = false
            val keyBoardHeight = if (isNavigationBarVisible) height - navigationBarHeight else height
//            "second keyBoardHeight:$keyBoardHeight lastHeight:$lastKeyBoardHeight dbHeight:$dbKeyboardHeight".logd(TAG)
            if (keyBoardHeight != lastKeyBoardHeight) { // 第二次计算高度和第一次计算高度不一致，以第二次为准
                if (dbKeyboardHeight != keyBoardHeight) { //保存的高度和第二次高度不一致更新保存的高度
                    dbKeyboardHeight = keyBoardHeight
                    navigationListener?.onSoftKeyBoardShow(keyBoardHeight)
                }
            }
        }

        if (height > 150 && !isKeyBoardVisible) { //软件盘打开
            isKeyBoardVisible = true
            val keyBoardHeight = if (isNavigationBarVisible) height - navigationBarHeight else height
//            "frist keyBoardHeight:$keyBoardHeight dbHeight:$dbKeyboardHeight".logd(TAG)
            if (keyBoardHeight == dbKeyboardHeight) { //计算的软件盘高度和保存的软件盘高度一致
                navigationListener?.onSoftKeyBoardShow(keyBoardHeight)
            } else { //计算的软件盘高度和保存的软件盘高度不一致
                secondCheck = true
                lastKeyBoardHeight = keyBoardHeight
                navigationListener?.onSoftKeyBoardShow(if (dbKeyboardHeight == 0) keyBoardHeight else dbKeyboardHeight)
            }

        } else if (height < 150 && isKeyBoardVisible) { //软件盘关闭
            isKeyBoardVisible = false
            navigationListener?.onSoftKeyBoardHide()
        }
    }

}