package com.walisport.module.live.utils.softkeyboard

interface KeyBoardListener {
    fun onAnimStart(moveDistance: Int)
    fun onAnimDoing(offsetX: Int, offsetY: Int)
    fun onAnimEnd()
}
