package arch.cayenne.module.chat.manager.interf

import arch.cayenne.module.chat.data.constants.KeyBoardType

/**
 * @author: wenxi
 * @date: 30/9/25 10:13
 * @description:
 */
interface  SoftKeyBoardMangerListener {
    fun keyboardChangeClick(keyBoardType: KeyBoardType,flag:Int = 0)

    fun changeKeyboardUi(keyBoardType:KeyBoardType)

    fun updateChatKeyboardType(keyBoardType: KeyBoardType)

}