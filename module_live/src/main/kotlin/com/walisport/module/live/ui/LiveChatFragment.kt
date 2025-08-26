package com.walisport.module.live.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.ui.view.DynamicStateLayout
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.database.entity.LiveMatchBean
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.lib.websocket.data.SocketConnectState
import com.walisport.module.live.R
import com.walisport.module.live.data.constants.KeyBoardType
import com.walisport.module.live.data.constants.KeyboardActionType
import com.walisport.module.live.data.constants.MatchStatus
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import com.walisport.module.live.ui.viewmodel.LiveMainViewModel
import com.walisport.module.live.utils.softkeyboard.KeyBoardInsetsCallBack
import com.walisport.module.live.utils.softkeyboard.KeyBoardListener
import com.walisport.module.live.utils.softkeyboard.LiveSoftKeyboardHelper
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>() {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<LiveChatViewModel> = LiveChatViewModel::class
    private val mainViewModel: LiveMainViewModel by sharedViewModel<LiveMainViewModel, LiveMainFragment>()
    private var emojiKeyBoardHeight:Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        initFragment()
        initTab()
        updateChatUi()
        showChat(8)
        mBinding.main.viewTreeObserver
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.main.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculationLayoutSize()
                    keyboardListener()
                }
            })
    }

    private fun calculationLayoutSize() {
        mBinding.apply {
            mViewModel.keyBoardHeight = mBinding.main.height
            emojiKeyBoardHeight = mViewModel.keyBoardHeight - 62.dp2px -21.dp2px
            liveChatKeyboard.layoutParams.height = emojiKeyBoardHeight
            layoutList.layoutParams.height = mViewModel.keyBoardHeight
            main.layoutParams.height = mViewModel.keyBoardHeight+emojiKeyBoardHeight
        }
    }

    private fun keyboardListener(){
        val keyBoardInsetsCallBack =
            KeyBoardInsetsCallBack(object : KeyBoardListener {
                override fun onAnimStart(moveDistance: Int) {
                    val animationType = mViewModel.getAnimationType(mViewModel.softKeyBoardListener.value, mViewModel.currentSoftKeyboard.value)
                    "onAnimStart $animationType softKeyBoardListener  ${mViewModel.softKeyBoardListener.value} currentSoftKeyboard ${ mViewModel.currentSoftKeyboard.value}".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    when (animationType) {
                        KeyboardActionType.SOFT_TO_EMOJI -> {
                        }
                        KeyboardActionType.EMOJI_TO_SOFT -> {
                            panelAnimateTo(-moveDistance)
                        }
                        KeyboardActionType.CHAT_TO_EMOJI -> {
                        }
                        KeyboardActionType.EMOJI_TO_CHAT -> {
                            panelAnimateTo(0)

                        }
                        KeyboardActionType.CHAT_TO_SOFT -> {
                        }
                        KeyboardActionType.SOFT_TO_SOFT ->{
                            mViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT)
                        }
                        KeyboardActionType.SOFT_TO_CHAT -> {
                        }
                        else ->{}
                    }
                }

                override fun onAnimDoing(offsetX: Int, offsetY: Int) {
                    val animationType = mViewModel.getAnimationType(mViewModel.softKeyBoardListener.value, mViewModel.currentSoftKeyboard.value)
                   "onDoing type $animationType $offsetY  ".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    if (animationType in arrayOf(KeyboardActionType.CHAT_TO_SOFT,KeyboardActionType.SOFT_TO_CHAT,KeyboardActionType.SOFT_TO_SOFT)) {
                        mBinding.main.translationY = offsetY.toFloat()
                    }
                }

                override fun onAnimEnd() {
                    val animationType = mViewModel.getAnimationType(mViewModel.softKeyBoardListener.value, mViewModel.currentSoftKeyboard.value)
                    "onAnimEnd   ${animationType in arrayOf(KeyboardActionType.CHAT_TO_SOFT,KeyboardActionType.SOFT_TO_CHAT,KeyboardActionType.EMOJI_TO_SOFT,KeyboardActionType.SOFT_TO_SOFT)} $animationType softKeyBoardListener  ${mViewModel.softKeyBoardListener.value} currentSoftKeyboard ${ mViewModel.currentSoftKeyboard.value}".logd("aaa")
                    if(animationType == KeyboardActionType.NONE){
                        return
                    }
                    if (animationType in arrayOf(KeyboardActionType.CHAT_TO_SOFT,KeyboardActionType.SOFT_TO_CHAT,KeyboardActionType.EMOJI_TO_SOFT,KeyboardActionType.SOFT_TO_SOFT)) {
                        mViewModel.updateKeyBoard()
                    }
                }
            })
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        ViewCompat.setWindowInsetsAnimationCallback(requireActivity().window.decorView, keyBoardInsetsCallBack)

//        ViewCompat.setWindowInsetsAnimationCallback(requireView(),object :
//            WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {
//            override fun onStart(
//                animation: WindowInsetsAnimationCompat,
//                bounds: WindowInsetsAnimationCompat.BoundsCompat
//            ): WindowInsetsAnimationCompat.BoundsCompat {
//                "onStart setWindowInsetsAnimationCallback".logd("aaa")
//                return super.onStart(animation, bounds)
//            }
//            override fun onProgress(
//                insets: WindowInsetsCompat,
//                runningAnimations: MutableList<WindowInsetsAnimationCompat>
//            ): WindowInsetsCompat {
//                return  insets
//            }
//
//            override fun onEnd(animation: WindowInsetsAnimationCompat) {
//                super.onEnd(animation)
//                "onEnd setWindowInsetsAnimationCallback".logd("aaa")
//
//            }
//        })
    }

    private fun panelAnimateTo(offset: Int,onStart:()->Unit ={},onEnd:()->Unit = {}) {
        val panelAnimator = ObjectAnimator.ofFloat(mBinding.main, "translationY", offset.toFloat())
        panelAnimator?.interpolator = FastOutSlowInInterpolator()
        panelAnimator?.addListener(onStart = {onStart.invoke()}, onEnd = {onEnd.invoke()})
        panelAnimator?.start()
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
        //软件盘时获取的高度有误，onResume时获取固定值
//        mBinding.liveChatKeyboard.translationY = -62.dp2px.toFloat()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initListener() {

        mBinding.main.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN && mViewModel.currentSoftKeyboard.value != KeyBoardType.CHAT) {
                showChat(9)
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_UP && mViewModel.currentSoftKeyboard.value != KeyBoardType.CHAT) {
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
            "onBack ${mViewModel.currentSoftKeyboard.value}".logd("aaa")
            if (mViewModel.currentSoftKeyboard.value != KeyBoardType.CHAT) {
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
        val fragment = LiveSoftKeyboardFragment()
        mBinding.apply {
            fragment.setInputView(liveChatIvEmoji, liveChatTvSend, liveChatIvKeyboard, liveChatEtInput, liveChatTvSize, liveChatLlInput,inputContent)
        }
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, LiveSoftKeyboardFragment.TAG).commit()
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
            mViewModel.softKeyBoardListener.collect {
//            val flag1 = !chatViewModel.checkSoftKeyboardVisible()
//            if (it != KeyBoardType.CHAT && flag1) {
//                chatViewModel.checkSoftKeyBoardBetAmount()
//                return@collect
//            }
                showChangeAnimation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
//     startSoftKeyboardHeight(requireActivity())
    }

//    fun startSoftKeyboardHeight(activity: FragmentActivity){
//        softKeyboardHeightHelper.startListener(activity)
//    }

    /**
     * 显示聊天界面时隐藏键盘界面
     * */
    private fun showChat(flag: Int) {
        mViewModel.addSoftKeyBoardEvent(KeyBoardType.CHAT, flag)
    }
    private fun showChangeAnimation() {
        val animationType = mViewModel.getAnimationType(
            mViewModel.softKeyBoardListener.value,
            mViewModel.currentSoftKeyboard.value
        )
        "showChangeAnimation $animationType listener: ${mViewModel.softKeyBoardListener.value} current ${ mViewModel.currentSoftKeyboard.value}".logd("aaa")
        when (animationType) {
            //展示软件盘
            KeyboardActionType.CHAT_TO_SOFT -> {
                mViewModel.updateSoftKeyBoard(true, 1)
            }
            //软件盘切换到聊天
            KeyboardActionType.SOFT_TO_CHAT -> {
                mViewModel.updateSoftKeyBoard(false, 2)
           }
//            //软件盘切换到表情键盘
            KeyboardActionType.SOFT_TO_EMOJI -> {
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    mViewModel.updateKeyBoard()
                    mViewModel.updateSoftKeyBoard(false, 3)
                })
//                startAnimation(animationType)
//                mViewModel.updateKeyBoard()
//                mViewModel.updateSoftKeyBoard(false, 3)
            }
            //展示表情键盘
            KeyboardActionType.CHAT_TO_EMOJI -> {
                panelAnimateTo(-emojiKeyBoardHeight, onStart = {
                    mViewModel.updateKeyBoard()
                })
//                startAnimation(animationType)
//                mViewModel.updateKeyBoard()
            }

            //表情键盘切换到软件盘
            KeyboardActionType.EMOJI_TO_SOFT -> {
                mViewModel.updateSoftKeyBoard(true, 4)
//                startAnimation(animationType)
//                mViewModel.updateKeyBoard()
//                mViewModel.updateSoftKeyBoard(true, 4)
            }
            //表情键盘切换到聊天
            KeyboardActionType.EMOJI_TO_CHAT -> {
                panelAnimateTo(0, onEnd = {
                    mViewModel.updateKeyBoard()
                })
//                startAnimation(animationType)
//                mViewModel.updateKeyBoard()
            }

            else -> {}
        }
    }



    /**
     * 判断键盘是否在显示中
     * */
    fun isSoftKeyboardVisible(): Boolean {
        val flag = mViewModel.currentSoftKeyboard.value != KeyBoardType.CHAT
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
        val allList = arrayListOf<ChatMsg>()
        allList.addAll(mViewModel.msgLists)
        adapter?.submitList(allList) {
            adapter.currentList.size.let {
                val position = it - 1
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
        if(true || matchBean?.liveInfo?.charRoom == true){
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
                liveChatKeyboard.isVisible = true
                dynamicState.setState(DynamicStateLayout.States.DATA_EMPTY,R.string.live_chat_first_chat.getString())
            } else {
                liveChatRecycler.isVisible = true
                liveChatKeyboard.isVisible = true
                dynamicState.isVisible = false
            }
        }
    }

    override fun onStop() {
        mViewModel.leaveRoom()
        LiveSoftKeyboardHelper.getInstance().unregisterKeyBoardListener()
        super.onStop()
    }
}