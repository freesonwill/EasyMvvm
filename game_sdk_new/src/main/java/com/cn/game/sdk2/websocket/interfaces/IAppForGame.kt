package com.cn.game.sdk2.websocket.interfaces

/**
 * app需要实现此接口
 * 用于sdk调用
 */
class GameApp {
    interface OnSdkListener {
        fun customerServiceAction()

        fun historyOfBetAction()

        fun onEnterGame()

        fun onEnterLive(type: Int, msg: String)

        fun onLeaveLive(type: Int, str: String?)

        fun onLoginGame(i: Int, str: String?)

        fun onTokenLoseEffectiveness()

        /**
         * 游戏主界面切换的回调
         * isShowUp: true为打开，false为关闭
         */
        fun onGameFloatingDetailViewStatus(isShowUp: Boolean)

        fun onInsufficientBalance()
    }
}

