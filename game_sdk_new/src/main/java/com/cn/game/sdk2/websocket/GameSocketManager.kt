package com.cn.game.sdk2.websocket

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.cn.game.sdk2.network.code.GameResCode
import com.cn.game.sdk2.utils.ThreadUtils
import com.cn.game.sdk2.websocket.imp.UIMethodImpl
import com.xcjh.base_lib2.utils.LogUtils
import com.xcjh.base_lib2.utils.LogUtilsExt
import com.xcjh.base_lib2.utils.LogUtilsExt.logd
import com.xcjh.base_lib2.utils.LogUtilsExt.loge
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.res.GameRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI


/**
 * sdk初始化-连接socket
 */
internal class GameSocketManager private constructor() : OnMessageListener {
    companion object {
        private val tag = GameSocketManager::class.java.name

        /**
         * 每隔10秒进行一次对长连接的心跳检测
         */
        private var HAS_HEART = true
        private var client: GameSocketClient? = null
        private var gameServerMessageConvertFactory: GameServerMessageConvertFactory? = null

        //@SuppressLint("StaticFieldLeak")
        private var INSTANCE: GameSocketManager? = null
        fun getInstance(): GameSocketManager? {
            if (INSTANCE == null) {
                synchronized(GameSocketManager::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = GameSocketManager()
                    }
                }
            }
            return INSTANCE
        }
    }
    private val scope get() = ThreadUtils.mainScope

    /******************* Method ***********************/
    fun initSocketClient(url: String) {
        if (isMainProcess()) {
            "initSocketClient".logd(tag)
            val uri = URI.create(url)
            isNeedReconnect = true
            HAS_HEART = true
            client = GameSocketClient(uri) //获得client对象
            client?.setOnMessageListener(this@GameSocketManager)
            gameMassageManager = UIMethodImpl.generate(client!!) //获得接口对象
            client?.connectionLostTimeout = 0
            client!!.connect() //连接socket
        }else{
            "initSocketClient --- Repeat operation".loge(tag)
        }
    }

    private fun isMainProcess(): Boolean {
        val packageName = appContext?.packageName
        val pid = Process.myPid()
        val manager = appContext?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                // 对比进程名，进程名是否为主进程名
                "currentProcess->${processInfo.pid}".logd(tag)
                return processInfo.processName == packageName
            }
        }
        return false
    }

    private fun resetUserState() {
        isLogin = false
        isEnterRoom = false
        gameAboutModel.setLoginResult(false)
    }

    /**
     * 断开连接
     */
    private fun closeConnect() {
        kotlin.runCatching {
            client?.close()
        }.onFailure {
            it.printStackTrace()
            client = null
        }
    }

    fun stopService() {
        kotlin.runCatching {
            HAS_HEART = false
            closeConnect()
            client = null
            INSTANCE = null
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun setGameServerMessageConvertFactory(factory: GameServerMessageConvertFactory) {
        gameServerMessageConvertFactory = factory
    }

    override fun onMessage(mid: Int?, sid: Int?, byteArray: ByteArray) {
        convertMessage(mid, sid, byteArray)
    }

    /**
     * 因为sid无重复 暂时只用sid
     * @see [client-res.proto]
     */
    private fun convertMessage(mid: Int?, sid: Int?, byteArray: ByteArray) {
        sid?.apply {
            when (this) {
                GameResCode.S2C_ENTER_INFO -> gameServerMessageConvertFactory?.enterInfo(
                    GameRes.EnterInfo.parseFrom(
                        byteArray
                    )
                )

                GameResCode.S2C_GROUP_INFO -> gameServerMessageConvertFactory?.groupInfo(
                    GameRes.GroupInfo.parseFrom(
                        byteArray
                    )
                )

                GameResCode.S2C_ENTER_MINI_GAME_INFO -> gameServerMessageConvertFactory?.enterMiniGameInfo(
                    GameRes.EnterMiniGameInfo.parseFrom(byteArray)
                )

                GameResCode.S2C_LEAVE_MINI_GAME -> gameServerMessageConvertFactory?.leaveMiniGameInfo(
                    GameRes.LeaveMiniGames.parseFrom(byteArray)
                )

                GameResCode.S2C_BEGIN_ROUND -> gameServerMessageConvertFactory?.beginRound(
                    GameRes.BeginNewRound.parseFrom(
                        byteArray
                    )
                )

                GameResCode.S2C_BEGIN_DEAL -> gameServerMessageConvertFactory?.beginDeal(
                    GameRes.BeginDeal.parseFrom(byteArray)
                )

                GameResCode.S2C_BEGIN_SETTLE -> gameServerMessageConvertFactory?.beginSettle(
                    GameRes.BeginSettle.parseFrom(
                        byteArray
                    )
                )

                GameResCode.S2C_REFRESH_USER_SCORE -> gameServerMessageConvertFactory?.refreshUserProperties(
                    GameRes.RefreshUserScore.parseFrom(byteArray)
                )

                GameResCode.S2C_LEAVE_GROUP -> gameServerMessageConvertFactory?.leaveGroup(
                    GameRes.LeaveGroup.parseFrom(
                        byteArray
                    )
                )

                GameResCode.S2C_SYNC_AREA_BET_INFO -> gameServerMessageConvertFactory?.syncAreaBetInfoBack(
                    GameRes.SyncAreaBetInfo.parseFrom(byteArray)
                )

                GameResCode.S2C_USER_BET_RESULT -> gameServerMessageConvertFactory?.miniGameBetResult(
                    GameRes.MyMiniGameBetResult.parseFrom(byteArray)
                )

                GameResCode.SUB_LOGON_RESP__SUCCESS -> gameServerMessageConvertFactory?.loginSuccess(
                    ClientRes.InfoAfterLoginSuccess.parseFrom(byteArray)
                )

                GameResCode.SUB_LOGON_RESP__LOGIN_ERROR -> gameServerMessageConvertFactory?.loginError(
                    ClientRes.ErrorMessage.parseFrom(byteArray)
                )

                GameResCode.S2C_OTHER_ERROR -> gameServerMessageConvertFactory?.errorMessage(
                    ClientRes.ErrorMessage.parseFrom(byteArray)
                )

                GameResCode.S2C_CLEAR_TRENDS -> gameServerMessageConvertFactory?.clearTrendsBackBlock(
                    GameRes.ClearTrends.parseFrom(byteArray)
                )

                GameResCode.S2C_MULTI_USER_LOGIN -> gameServerMessageConvertFactory?.tokenLoseEffectiveness()

                GameResCode.S2C_SERVER_MAINTENANCE -> gameServerMessageConvertFactory?.tokenLoseEffectiveness()

                GameResCode.S2C_ROOM_TIMEOUT -> gameServerMessageConvertFactory?.tokenLoseEffectiveness()

                GameResCode.S2C_MULTI_TOKEN_VERIFY_FAIL -> gameServerMessageConvertFactory?.tokenLoseEffectiveness()

                GameResCode.S2C_REFRESH_GAME_CONFIG -> gameServerMessageConvertFactory?.refreshGameConfig(
                    GameRes.RefreshGameConfig.parseFrom(byteArray)
                )
                GameResCode.S2C_REFRESH_WALI_GAME_PLAYER_COUNT ->{
                    scope.launch(Dispatchers.Main) {
                        val data = withContext(Dispatchers.IO) {
                            val ret = GameRes.RefreshWaliGamePlayerCount.parseFrom(byteArray)
                            LogUtils.d("onMessage player count is ${ret.playerCountsList.size}, ${ret.playerCountsList}")
                            ret
                        }
                        gameServerMessageConvertFactory?.refreshGamePlayerCount(data)
                    }
                }
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        if (remote && (code == 1000 || code == 1001) && !isTokenValid) {
            isNeedReconnect = false
            resetUserState()
        }
    }

}

interface OnMessageListener {
    fun onMessage(mid: Int?, sid: Int?, byteArray: ByteArray)

    fun onClose(code: Int, reason: String?, remote: Boolean)
}