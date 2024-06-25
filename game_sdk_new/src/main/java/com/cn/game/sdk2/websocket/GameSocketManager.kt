package com.cn.game.sdk2.websocket

import android.annotation.SuppressLint
import com.cn.game.sdk2.network.code.GameResCode
import com.cn.game.sdk2.websocket.imp.UIMethodImpl
import com.cn.game.sdk2.websocket.viewmodel.MessageViewModel
import com.xcjh.base_lib.utils.loge
import game.common.proto.ClientRes
import game.mod.proc.yf.proto.res.GameRes
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.java_websocket.enums.ReadyState
import java.net.URI

/**
 * sdk初始化-连接socket
 */
class GameSocketManager private constructor() : OnMessageListener {
    companion object {
        private val tag = GameSocketManager::class.java.name

        /**
         * 每隔10秒进行一次对长连接的心跳检测
         */
        private const val HEART_BEAT_RATE = (10 * 1000).toLong()
        private var reconnectCount = 0
        private var HAS_HEART = true
        private var client: GameSocketClient? = null
        private var gameServerMessageConvertFactory: GameServerMessageConvertFactory? = null

//        var gameService: GameServiceImp? = null

        @SuppressLint("StaticFieldLeak")
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

    @OptIn(DelicateCoroutinesApi::class)
    fun initSocketClient() {
        "initSocketClient".loge(tag)
        val uri = URI.create(WEB_SOCKET_URL)
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                client = GameSocketClient(uri) //获得client对象
                client?.reset()
                client?.setOnMessageListener(this@GameSocketManager)
                gameMassageManager = UIMethodImpl.generate(client!!) //获得接口对象

                client?.connectionLostTimeout = 0
                client!!.connectBlocking() //连接socket
                //心跳发送
                while (true) {
                    delay(HEART_BEAT_RATE)
                    if (HAS_HEART) {
                        client?.let {
                            if (it.readyState == ReadyState.OPEN) {
                                gameMassageManager?.ping()
                            }//正常发送心跳
                            if (it.isClosed) it.re()
                        }
                    } else {
                        client?.let {
                            if (it.readyState == ReadyState.OPEN) HAS_HEART = true //socket恢复
                        }
                    }
                }
            }
        }
        messageViewModel = MessageViewModel()
        messageViewModel?.data?.observeForever {
            "observeForever".loge()
            val newUnpack = client?.newUnpack(it)
            val mid = newUnpack!![0] as Int?
            val sid = newUnpack[1] as Int?
            var str = ByteArray(0)
            if (it.size > 2) {
                str = (newUnpack[2] as ByteArray?)!!
            }
            onMessage(mid, sid, str)
        }
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

    fun getGameService(): UIMethodImpl? {
        return gameMassageManager
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

                GameResCode.S2C_SERVER_MAINTENANCE -> gameServerMessageConvertFactory?.serverMaintenance()

                GameResCode.S2C_ROOM_TIMEOUT -> gameServerMessageConvertFactory?.roomTimeout()

            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
//        client?.re()
    }

}

interface OnMessageListener {
    fun onMessage(mid: Int?, sid: Int?, byteArray: ByteArray)

    fun onClose(code: Int, reason: String?, remote: Boolean) {
        "GameSocketClose-code:$code".loge()
        "GameSocketClose-reason:$reason".loge()
        "GameSocketClose-remote:$remote".loge()
    }
}