package arch.cayenne.module.chat.utils.softkeyboard

interface KeyBoardListener {
    fun onAnimStart(keyboardHeight: Int,animDuration:Long)
    fun onAnimDoing(offsetX: Int, offsetY: Int)
    fun onAnimEnd()
}
