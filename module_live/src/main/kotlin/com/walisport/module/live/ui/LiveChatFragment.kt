package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>() {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<LiveChatViewModel> = LiveChatViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initFragment()
        initTab()
    }

    private fun initTab() {
        val layoutManger = LinearLayoutManager(context)
        val adapter = LiveChatAdapter()
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
        mBinding.liveChatRecycler.itemAnimator = null
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
        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP && mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
                    showChat(7)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

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

    private fun initFragment() {
        mViewModel.setKeyBoardHeight()
        val fragment = LiveSoftKeyboardFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, LiveSoftKeyboardFragment.TAG)
            .commit()
        mBinding.liveChatKeyboard.post {
            mViewModel.keyBoardHeight = mBinding.liveChatKeyboard.height
            if(mViewModel.keyBoardHeight == 0){
                mViewModel.keyBoardHeight = mBinding.main.height
            }
            fragment.addMainViewListen()
        }
    }

    override suspend fun createObserver() {
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setArguments(mainViewModel.matchId.value)
        }

        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            updateChatUi(it)
        }

        mViewModel.loginLiveData.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.enterRoom()
            }
        }

        mViewModel.enterRoomLiveData.observe(viewLifecycleOwner) {

        }

        mViewModel.sendMsgLiveData.observe(viewLifecycleOwner) {
            mViewModel.addLocalMsg(it)
            mViewModel.sendMsgToServer(it)
            refreshChatList()
        }

        mViewModel.historyLiveData.observe(viewLifecycleOwner) {
            refreshChatList()
        }
        mViewModel.sendMsgResultLiveData.observe(viewLifecycleOwner) {

        }

        mViewModel.checkBetAmountLiveData.observe(viewLifecycleOwner) {
        }

        mViewModel.toastLiveData.observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                mViewModel.newMsgFlow.collect {
                    it?.let { msg ->
                        mViewModel.addNewMsgs(msg)
                        refreshChatList()
                    }
                }
            }
            launch {
                mViewModel.getConnectStateFlow().collect {
                    if (it == SocketConnectState.Connecting && mViewModel.loginLiveData.value == null) {
                        mViewModel.chatLogin()
                    }
                }
            }

        }
    }

    /**
     * 显示聊天界面时隐藏键盘界面
     * */
    private fun showChat(flag: Int) {
        mViewModel.updateKeyBoardUi(KeyBoardType.CHAT,flag)
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
     * 接收到新数据做更新
     * */
    private fun refreshChatList() {
        if (!mBinding.liveChatRecycler.isVisible && mViewModel.msgLists.isNotEmpty()) {
            updateChatList()
        } else if (mBinding.liveChatRecycler.isVisible && mViewModel.msgLists.isEmpty()) {
            updateChatList()
        }
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as LiveChatAdapter }
        adapter?.submitList(mViewModel.msgLists) {
            adapter.currentList.size.let {
                val position = it - 1
                "refreshChatList ${position}".logd("aaa")
                if (position > 0) {
                    mBinding.liveChatRecycler.scrollToPosition(position)
                }
            }
        }
    }

    /**
     * 进入直播间不成功时修改
     * */
    fun updateChatUi(matchBean:LiveMatchBean? = null) {
        if(matchBean?.liveInfo?.charRoom == true){
            updateChatList()
            return
        }

        //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
        val code = mainViewModel.mainMatch.value?.basicInfo?.status
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
            if (mViewModel.msgLists.isEmpty()) {
                liveChatRecycler.isVisible = false
                dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY,R.string.live_chat_first_chat.getString())
            } else {
                liveChatRecycler.isVisible = true
                dynamicState.isVisible = false
            }


        }
    }

    override fun onStop() {
        mViewModel.leaveRoom()
        super.onStop()
    }
}