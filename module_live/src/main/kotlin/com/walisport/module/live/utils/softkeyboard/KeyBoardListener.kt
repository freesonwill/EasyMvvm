package com.walisport.module.live.utils.softkeyboard

interface KeyBoardListener {
    fun onAnimStart(keyboardHeight: Int,animDuration:Long)
    fun onAnimDoing(offsetX: Int, offsetY: Int)
    fun onAnimEnd()
}
