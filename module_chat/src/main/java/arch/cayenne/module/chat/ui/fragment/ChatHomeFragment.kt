package arch.cayenne.module.chat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.addCallback
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.MatchStatus
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.database.entity.LiveMatchBasicInfoBean
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.data.SocketConnectState
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.databinding.FragmentLiveChatBinding
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class ChatHomeFragment : BaseFragment<ChatHomeViewModel, FragmentLiveChatBinding>() {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<ChatHomeViewModel> = ChatHomeViewModel::class
    var mainMatch:LiveData<LiveMatchBean>? = null
    var matchIdLiveData:LiveData<Long>? = null

    override fun initView(savedInstanceState: Bundle?) {
        initChatPageFragment()
        initSoftKeyBoardFragment()
        arguments?.let { //TODO  首页过来的 之后需要处理聊天室要matchId的问题
            val value = it.getBoolean("chat",false)
            setMainChatStatus()
        }
    }

    override fun onStart() {
        super.onStart()
        mBinding.liveChatKeyboard.post {
            val height = mBinding.liveChatKeyboard.height
            mViewModel.setChatHeight(height)
        }
    }

    override suspend fun createObserver() {
        matchIdLiveData?.observe(viewLifecycleOwner){
            observeMatchId(it)
        }
        mainMatch?.observe(viewLifecycleOwner){
            observeLiveMatch(it)
        }
        mViewModel.chatHistoryIsEmpty.observe(viewLifecycleOwner){
            updateChatUi(mainMatch?.value)
        }
        lifecycleScope.launch {
            launch {
                mViewModel.serverFlow().collect{
                    if (it == SocketConnectState.Connecting && mViewModel.loginFlow.value == null) {
                        mViewModel.chatLogin()
                    }
                }
            }
            launch {
                mViewModel.sendMsgToServerFlow.collect {

                }
            }
        }
    }

    override fun onFragmentAnimEnd(isEnter: Boolean) {
        super.onFragmentAnimEnd(isEnter)
        showChat(6)
    }

    override fun onResume() {
        super.onResume()
        //软件盘时获取的高度有误，onResume时获取固定值
//        mBinding.liveChatKeyboard.translationY = -62.dp2px.toFloat()
    }

    override fun onPause() {
        super.onPause()
        showChat(7)//移动到其他页面后关闭软件盘表情键盘
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mBinding.main.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN && mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
                showChat(9)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
                showChat(1)
            } else {
                if (activity == null) {
                    return@addCallback
                }
                isEnabled = false
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun initSoftKeyBoardFragment() {
        val fragment = SoftKeyboardFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, SoftKeyboardFragment.TAG)
            .commit()
    }

    private fun initChatPageFragment(){
        val fragment = ChatPageFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatHistory.id,fragment,ChatPageFragment.TAG)
            .commit()
    }



    /**
     * 显示聊天界面时隐藏键盘界面
     * */
    private fun showChat(flag: Int) {
        mViewModel.updateKeyBoardUi(KeyBoardType.CHAT, flag)
    }

    /**
     * 判断键盘是否在显示中
     * */
    fun isSoftKeyboardVisible(): Boolean {
        val flag = mViewModel.currentKeyBoardType != KeyBoardType.CHAT
        if (flag) {
            showChat(3)
        }
        return flag
    }



    /**
     * 进入直播间不成功时修改
     * */
    fun updateChatUi(matchBean: LiveMatchBean? = null) {
        if (matchBean?.liveInfo?.charRoom == true || matchBean == null) {
            updateChatList()
            return
        }
//        //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
        val code = matchBean?.basicInfo?.status
        val status = MatchStatus.entries.find { status -> status.code == code }
        mBinding.also {
            when (status) {
                MatchStatus.FINISHED, MatchStatus.CANCELED, MatchStatus.ABANDONED -> {
                    it.liveChatGroupChat.isVisible = false

                    it.dynamicState.setState(DynamicStateLayout.States.CLOSE,R.string.live_chat_end.getString())
                }
                else -> {
                    it.liveChatGroupChat.isVisible = false
                    it.dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY,R.string.live_chat_empty.getString())
                }
            }
        }
    }

    private fun updateChatList() {
        mBinding.apply {
            if (mViewModel.chatHistoryIsEmpty.value == true) {
                liveChatHistory.isInvisible = true
                dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY, R.string.live_chat_first_chat.getString())
            } else {
                liveChatHistory.isInvisible = false
                dynamicState.isVisible = false
            }
        }
    }

    override fun onStop() {
        mViewModel.leaveRoom()
        super.onStop()
    }
    /**
     * 直播间调用
     * */
    fun setMatchLiveData(matchId:LiveData<Long>?,mainMatch:LiveData<LiveMatchBean>?){
        matchIdLiveData = matchId
        this.mainMatch = mainMatch
    }

    /**
     * 首页调用
     * */
    private fun setMainChatStatus(){
       lifecycleScope.launchWhenResumed {
           observeMatchId(-1)
           observeLiveMatch(null)
       }
    }

    private fun observeMatchId(matchId: Long) {
            mViewModel.setArguments(matchId)
    }

   private fun observeLiveMatch(match: LiveMatchBean?) {
       mViewModel.matchStatus = match == null
        updateChatUi(match)
        //比赛开始后开启聊天服务
        if (match?.liveInfo?.charRoom == true || match == null) {
            mViewModel.startChatServer()
        }
    }

    fun closeChatWebsocket() {
        mViewModel.disConnectChatServer()
    }

}