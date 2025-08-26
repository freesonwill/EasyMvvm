package com.walisport.module.live.utils.softkeyboard

/**
 * @author: wenxi
 * @date: 25/8/25 16:39
 * @description:
 */
class LiveSoftKeyboardHelper private constructor() {
    companion object{
        private var instance:LiveSoftKeyboardHelper? = null
        fun getInstance():LiveSoftKeyboardHelper{
            return instance?: LiveSoftKeyboardHelper()
        }
    }
    private var listener:KeyBoardListener? = null

    fun registerKeyBoardListener(listener: KeyBoardListener){
        this.listener = listener
    }

    fun unregisterKeyBoardListener(){
        this.listener = null
    }

    fun onAnimStart(moveDistance: Int){
        this.listener?.onAnimStart(moveDistance)
    }
    fun onAnimDoing(offsetX: Int, offsetY: Int){
        this.listener?.onAnimDoing(offsetX,offsetY)

    }
    fun onAnimEnd(){
        this.listener?.onAnimEnd()
    }
}