package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.data.SocketConnectState
import kotlinx.coroutines.launch

/**
 * @author: wenxi
 * @date: 25/12/25 00:07
 * @description:
 */
class ChatMainFragment : ChatBaseFragment() {
    override val chatType: ChatType
        get() = ChatType.GAME
    override val isMainSoft: Boolean
        get() = true

    override fun listenParentFragment() {
        parentFragmentManager.setFragmentResultListener(FRAGMENT_RESULT_KEY, this) { key, bundle ->
            val matchId = bundle.getLong(MATCH_ID_KEY, -1L)
            if (matchId != -1L) {
                observeMatchId(matchId)
            }
            //首页直接开启聊天状态
            observeLiveMatch(true, 5)
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mViewModel.startChatServer()

    }


    override fun onStart() {
        super.onStart()
        checkLogin()
        mViewModel.enterRoom(chatType)
    }

    override fun onStop() {
        super.onStop()
        mViewModel.leaveRoom(chatType)
    }

    override fun onDestroy() {
        super.onDestroy()
        //首页的直播间不用退出房间
        mViewModel.disConnectChatServer()
    }

    override suspend fun createObserver() {
        super.createObserver()
        lifecycleScope.launch {
            mViewModel.loginFlow.collect{
                if(it?.code == 0)    {
                    mViewModel.enterRoom(chatType)
                }
            }
        }
    }

    /**
     * 如果服务开启了登陆没有成功，就重新登陆一次
     * */
    private fun checkLogin() {
        lifecycleScope.launch {
            if (mViewModel.serverFlow().value == SocketConnectState.Connecting && (mViewModel.loginFlow.value == null || mViewModel.loginFlow.value?.code != 0)) {
                mViewModel.chatLogin(chatType)
            }
        }
    }

//    mViewModel.startChatServer()
// chatLogin  enterRoom leaveRoom相关逻辑待定

}