package com.walisport.module.live.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.CheckBetResultEnum
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>(),
    LiveSoftKeyboardFragment.LiveChatSoftKeyListener {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<LiveChatViewModel> = LiveChatViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()

    //软件盘时获取的高度有误，onResume时获取固定值
    private var keyBoardHeight: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        initFragment()
        initTab()
        updateChatUi()
    }

    private fun initTab() {
        val layoutManger = LinearLayoutManager(context)
        val adapter = LiveChatAdapter()
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        keyBoardHeight = mBinding.main.height
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP && mViewModel.softKeyBoardListener.value == true) {
                    showChat()
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (mViewModel.softKeyBoardListener.value == true) {
                showChat()
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
        val fragment = LiveSoftKeyboardFragment()
        fragment.setSoftKeyListener(this)
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, LiveSoftKeyboardFragment.TAG).commit()
    }

    override fun createObserver() {
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setArguments(mainViewModel.matchId.value)
        }

        mViewModel.loginLiveData.observe(viewLifecycleOwner) {
            it?.let {
                mViewModel.enterRoom()
            }
        }

        mViewModel.enterRoomLiveData.observe(viewLifecycleOwner) {

        }

        mViewModel.sendMsgLiveData.observe(viewLifecycleOwner) {
            val checkBetAmount = mViewModel.checkBetAmountLiveData.value
            if (checkBetAmount != CheckBetResultEnum.SUCCESS) {
                val msg =
                    if (checkBetAmount == CheckBetResultEnum.BET_AMOUNT) getString(R.string.insufficient_bet_amount)
                    else getString(R.string.insufficient_balance)
                showToast(msg)
                return@observe
            }
            if (mViewModel.loginLiveData.value == null) {
                return@observe
            }
            mViewModel.sendMsgToServer(it)
            mViewModel.addLocalMsg(it)
            refreshChatList()
        }

        mViewModel.historyLiveData.observe(viewLifecycleOwner) {
            refreshChatList()
        }
        mViewModel.sendMsgResultLiveData.observe(viewLifecycleOwner) {

        }

        mViewModel.checkBetAmountLiveData.observe(viewLifecycleOwner) {
            if (it != CheckBetResultEnum.SUCCESS) {
                updateChatUi()
            }
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
//                    "startChatserver flow $it shoulderLogin:${mViewModel.loginLiveData.value == null}".logd("chat")
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
    private fun showChat() {
        getSoftKeyBoardFragment()?.showChat()
    }

    private fun getSoftKeyBoardFragment(): LiveSoftKeyboardFragment? {
        return childFragmentManager.findFragmentByTag(LiveSoftKeyboardFragment.TAG)?.let {
            val keyboardFragment = it as LiveSoftKeyboardFragment
            keyboardFragment
        }

    }

    private fun showChatAnimation(isKeyBoardVisible: Boolean, isEmoji: Boolean) {
        mBinding.liveChatKeyboard.layoutParams.height =
            if (isKeyBoardVisible && isEmoji) keyBoardHeight else 62.dp2px
    }

    /**
     * 显示键盘时调用
     * */
    override fun showKeyBoard(isEmoji: Boolean) {
        showChatAnimation(true, isEmoji)
        mViewModel.updateSoftKeyBoard(true)
    }

    /**
     * 隐藏键盘时调用
     * */
    override fun hideKeyboard() {
        showChatAnimation(false, isEmoji = false)
        mViewModel.updateSoftKeyBoard(false)
    }

    /**
     * 判断键盘是否在显示中
     * */
    fun isSoftKeyboardVisible(): Boolean {
        val flag = mViewModel.softKeyBoardListener.value ?: false
        if (flag) {
            showChat()
        }
        return flag
    }

    /**
     * 接收到新数据做更新
     * */
    private fun refreshChatList() {
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as LiveChatAdapter }
        val allList = arrayListOf<ChatMsg>()
        allList.addAll(mViewModel.msgLists)
        adapter?.submitList(allList) {
            adapter.currentList.size.let {
                val position = it - 1
                if (position > 0) {
                    mBinding.liveChatRecycler.smoothScrollToPosition(position)
                }
            }
        }
    }

    /**
     * 进入直播间不成功时修改
     * */
    fun updateChatUi() {
        //比赛状态 0-已结束 1-推迟 2-中断 3-取消 4-未开赛 5-进行中 6-延迟 7-废弃 8-暂停
        val code = mainViewModel.mainMatch.value?.basicInfo?.status
        val status = MatchStatus.entries.find { status -> status.code == code }
        mBinding.also {
            when (status) {
                MatchStatus.FINISHED, MatchStatus.CANCELED, MatchStatus.ABANDONED -> {
                    it.liveChatGroupChat.isVisible = false
                    it.liveChatGroupStatus.isVisible = true
                    it.liveChatIvStatus.setImageResource(R.drawable.live_chat_is_closed)
                    it.liveChatTvStatus.setText(R.string.live_chat_end)
                }

                MatchStatus.POSTPONED, MatchStatus.NOT_STARTED, MatchStatus.DELAYED -> {
                    it.liveChatGroupChat.isVisible = false
                    it.liveChatGroupStatus.isVisible = true
                    it.liveChatIvStatus.setImageResource(R.drawable.live_chat_is_empty)
                    it.liveChatTvStatus.setText(R.string.live_chat_empty)
                }

                else -> {
                    it.liveChatGroupChat.isVisible = true
                    it.liveChatGroupStatus.isVisible = false
                }
            }
        }
    }


    override fun onStop() {
        mViewModel.leaveRoom()
        super.onStop()
    }
}