package arch.cayenne.module.chat.manager

/**
 * @author: wenxi
 * @date: 1/9/25 10:37
 * @description: 软件盘弹出隐藏监听
 */
interface SoftAnimListener {
    fun setNavigationStatus(hasNavigation:Boolean,navigationHeight:Int)
    fun onSoftKeyBoardHide()
    fun onSoftKeyBoardShow(keyboardHeight:Int)
    fun secondSoftKeyBoardShow()

}