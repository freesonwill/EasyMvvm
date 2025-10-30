package arch.cayenne.module.chat.manager

/**
 * @author: wenxi
 * @date: 1/9/25 10:37
 * @description: 实时更新虚拟导航栏的高度
 */
interface SoftAnimListener {
    fun setNavigationStatus(hasNavigation:Boolean,navigationHeight:Int)
    fun onSoftKeyBoardHide()
    fun onSoftKeyBoardShow(keyboardHeight:Int)
    fun secondSoftKeyBoardShow()

}