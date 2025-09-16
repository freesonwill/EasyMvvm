package com.walisport.module.live.utils.softkeyboard

/**
 * @author: wenxi
 * @date: 1/9/25 10:37
 * @description: 实时更新虚拟导航栏的高度
 */
interface NavigationListener {
    fun setNavigationStatus(hasNavigation:Boolean,navigationHeight:Int)
    fun onSoftKeyBoardHide()
    fun onSoftKeyBoardShow(keyboardHeight:Int)

}