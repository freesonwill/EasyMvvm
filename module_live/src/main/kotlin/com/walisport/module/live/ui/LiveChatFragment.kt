package com.walisport.module.live.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.addCallback
import androidx.core.animation.addListener
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
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>(){
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
        mBinding.liveChatRecycler.itemAnimator = null
    }

    override fun onResume() {
        super.onResume()
        keyBoardHeight = mBinding.main.height
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP && mViewModel.currentSoftKeyboard.value != KeyBoardType.NONE) {
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
            if (mViewModel.currentSoftKeyboard.value != KeyBoardType.NONE) {
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

    override fun onPause() {
        super.onPause()
        showChat()
    }

    private fun initFragment() {
        val fragment = LiveSoftKeyboardFragment()
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, LiveSoftKeyboardFragment.TAG).commit()
    }

    override suspend fun createObserver() {
        mainViewModel.matchId.observe(viewLifecycleOwner) {
            mViewModel.setArguments(mainViewModel.matchId.value)
        }

        mainViewModel.mainMatch.observe(viewLifecycleOwner) {
            updateChatUi()
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
                    if (checkBetAmount == CheckBetResultEnum.BET_AMOUNT_INVALID) getString(R.string.insufficient_bet_amount)
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
            launch {
                mViewModel.softKeyBoardListener.collect {
                    val flag1 = !mViewModel.checkSoftKeyboardVisible()
                    if (it != KeyBoardType.NONE && flag1) {
                        mViewModel.checkSoftKeyBoardBetAmount()
                        return@collect
                    }
                    when (it) {
                        KeyBoardType.EMOJI -> {
                            showChatAnimation(true, true)
                        }
                        else -> {
                            if (mViewModel.currentSoftKeyboard.value == KeyBoardType.EMOJI) {
                                showChatAnimation(false, true)
                            } else {
                                showChatAnimation(false, isEmoji = false)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 显示聊天界面时隐藏键盘界面
     * */
    private fun showChat( ) {
        mViewModel.addSoftKeyBoardEvent(KeyBoardType.NONE)
    }

    /**
     * 如果要隐藏和显示表情键盘时使用动画
     * */
    private fun showChatAnimation(emojiKeyBoardVisible: Boolean, isEmoji: Boolean) {
        val height = if (emojiKeyBoardVisible && isEmoji) keyBoardHeight else 62.dp2px
        if (isEmoji) {
            val params = if (emojiKeyBoardVisible) floatArrayOf(keyBoardHeight.toFloat(), 0f) else floatArrayOf(0f, (mViewModel.softKeyBoardHeight).toFloat())
            val alphaParam =if(emojiKeyBoardVisible) floatArrayOf(0f,1f) else floatArrayOf( 1f,0f)

            val transAnimation = ObjectAnimator.ofFloat(mBinding.liveChatKeyboard, "translationY", *params)
            val alphaAnimation = ObjectAnimator.ofFloat(mBinding.liveChatKeyboard,"alpha",*alphaParam)
            val animatorSet = AnimatorSet()
            animatorSet.addListener(onStart = {
                if (emojiKeyBoardVisible) {
                    mBinding.liveChatKeyboard.layoutParams.height = height
                    mViewModel.updateSoftKeyBoard()
                }else{
                    if(mViewModel.softKeyBoardListener.value == KeyBoardType.SOFT_KEYBOARD){
                        mViewModel.openSoftKeyBoard(KeyBoardType.SOFT_KEYBOARD)
                    }
                }
            }, onEnd = {
                if (!emojiKeyBoardVisible) {
                    mBinding.liveChatKeyboard.layoutParams.height = height
                    mBinding.liveChatKeyboard.translationY = 0f
                    mViewModel.updateSoftKeyBoard()
                }
                mBinding.liveChatKeyboard.alpha = 1f
            })
            animatorSet.duration = 300L
            animatorSet.playTogether(transAnimation,alphaAnimation)
            animatorSet.start()
        } else {
            mViewModel.updateSoftKeyBoard()
            mBinding.liveChatKeyboard.layoutParams.height = height
        }
    }

    /**
     * 判断键盘是否在显示中
     * */
    fun isSoftKeyboardVisible(): Boolean {
        val flag = mViewModel.currentSoftKeyboard.value != KeyBoardType.NONE
        if (flag) {
            showChat()
        }
        return flag
    }

    /**
     * 接收到新数据做更新
     * */
    private fun refreshChatList() {
         if(!mBinding.liveChatRecycler.isVisible && mViewModel.msgLists.isNotEmpty()){
             updateChatList()
         }else if(mBinding.liveChatRecycler.isVisible && mViewModel.msgLists.isEmpty()){
             updateChatList()
         }
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
                    it.liveChatIvStatus.setBackgroundResource(arch.cayenne.lib.common.R.drawable.icon_close)
                    it.liveChatTvStatus.setText(R.string.live_chat_end)
                }
                MatchStatus.IN_PROGRESS, MatchStatus.PAUSED, MatchStatus.INTERRUPTED -> {
                    updateChatList()
                }
                else -> {
                    it.liveChatGroupChat.isVisible = false
                    it.liveChatGroupStatus.isVisible = true
                    it.liveChatIvStatus.setBackgroundResource(arch.cayenne.lib.common.R.drawable.icon_empty)
                    it.liveChatTvStatus.setText(R.string.live_chat_empty)
                }
            }
        }
    }

    private fun updateChatList() {
        mBinding.apply {
            if (mViewModel.msgLists.isEmpty()) {
                liveChatRecycler.isVisible = false
                liveChatKeyboard.isVisible = true
                liveChatGroupStatus.isVisible = true
                liveChatIvStatus.setBackgroundResource(arch.cayenne.lib.common.R.drawable.icon_empty)
                liveChatTvStatus.setText(R.string.live_chat_first_chat)
            }else{
                liveChatRecycler.isVisible = true
                liveChatKeyboard.isVisible = true
                liveChatGroupStatus.isVisible = false
            }
        }
    }

    override fun onStop() {
        mViewModel.leaveRoom()
        super.onStop()
    }
}