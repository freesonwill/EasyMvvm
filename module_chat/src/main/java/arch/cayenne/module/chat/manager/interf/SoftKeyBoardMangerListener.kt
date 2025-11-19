package arch.cayenne.module.chat.manager.interf

import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType

/**
 * @author: wenxi
 * @date: 30/9/25 10:13
 * @description:
 */
interface  SoftKeyBoardMangerListener {
    fun keyboardChangeClick(keyBoardType: KeyBoardType,flag:Int = 0)

    fun changeKeyboardUi(keyBoardType:KeyBoardType)

    fun updateChatKeyboardType(keyBoardType: KeyBoardType)

    fun startAnim(actionType: KeyboardActionType,offset: Int, onStart: () -> Unit = {}, onEnd: () -> Unit = {})

    /***
     *由于直播间拉伸时main的高度不断变化
     * 上移的距离需要根据拉伸高度而减少
     */
    fun getMainHeight():Int

}