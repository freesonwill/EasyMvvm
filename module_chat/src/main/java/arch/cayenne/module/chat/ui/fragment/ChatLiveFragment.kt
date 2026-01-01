package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.data.SocketConnectState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author: wenxi
 * @date: 25/12/25 00:07
 * @description:
 */
class ChatLiveFragment : ChatBaseFragment() {
    override val chatType: ChatType
        get() = ChatType.LOBBY
    override val isMainSoft: Boolean
        get() = false

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        checkStartServer()
        checkLogin()
        enterRoom()
    }

    override fun listenParentFragment() {
        parentFragmentManager.setFragmentResultListener(FRAGMENT_RESULT_KEY, this) { key, bundle ->
            val matchId = bundle.getLong(MATCH_ID_KEY, -1L)
            if (matchId != -1L) {
                observeMatchId(matchId)

            }
            val matchStatus = bundle.getInt(MATCH_STATUS_KEY, -1)
            val matchStart = bundle.getBoolean(LIVE_START_KEY, false)
            if (matchStatus != -1) {
                observeLiveMatch(matchStart, matchStatus)
            }
        }
    }

    private fun checkStartServer() {
        lifecycleScope.launch {
            if (mViewModel.serverFlow().value != SocketConnectState.Connecting) {
                mViewModel.startChatServer()
            }
        }
    }

    private fun checkLogin() {
        lifecycleScope.launch {
            if (mViewModel.loginFlow.value?.code != 0) {
                mViewModel.chatLogin(chatType)
            }
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.loginFlow.collect {
            if (it?.code == 0) {
                mViewModel.enterRoom(chatType)
            }
        }
    }

    private fun enterRoom() {
        lifecycleScope.launch {
            delay(500)
            mViewModel.enterRoom(chatType)
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.leaveRoom(chatType)
    }


    //比赛开始后开启聊天服务 TODO 直播间进入聊天室逻辑待定
//        if (match?.liveInfo?.charRoom == true || match == null) {
//    mViewModel.startChatServer()
//        }


// chatLogin  enterRoom leaveRoom相关逻辑待定


}