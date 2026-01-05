package arch.cayenne.module.chat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.websocket.chat.data.ChatType
import arch.cayenne.lib.websocket.chat.data.MsgType
import arch.cayenne.module.chat.data.model.ChatMsgPageBean
import arch.cayenne.module.chat.databinding.FragementChatPageLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatPageAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatPageViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 1/10/25 15:09
 * @description:
 */
class ChatPageFragment : BaseFragment<ChatPageViewModel, FragementChatPageLayoutBinding>() {
    override val vbClass: KClass<FragementChatPageLayoutBinding>
        get() = FragementChatPageLayoutBinding::class
    override val vmClass: KClass<ChatPageViewModel>
        get() = ChatPageViewModel::class
    private val homeViewModel: ChatHomeViewModel by sharedViewModel<ChatHomeViewModel, ChatBaseFragment>()


    override fun initView(savedInstanceState: Bundle?) {
        ChatPersonalDialogFragment.create(this)
        BetShareDialogFragment.create(this)
        ChatPrivateUserFragment.create(this)
        initRecycler()
        listenFragmentResult()
    }

    var selectBean: ChatMsgPageBean? = null

    private fun initRecycler() {
        val layoutManger = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
            reverseLayout = true
        }
        val adapter = ChatPageAdapter(
            specialClick = { bean, clickSpane, clickType ->
                "click bean.msgType=${bean.msgType},clickType=$clickType".logd(TAG)
                if (bean.msgType == ChatMsgType.SYSTEM) {
                    return@ChatPageAdapter
                }

                selectBean = bean
                when (clickType) {
                    ChatMsgType.BET_GAME -> {
//                    val betType = if(clickSpane == "注单游戏") 0 else 1
                        BetShareDialogFragment.show(this, 0)
                    }

                    ChatMsgType.BET_SPORT -> {
                        BetShareDialogFragment.show(this, 1)
                    }

                    ChatMsgType.AT -> {
                        ChatPrivateUserFragment.show(this)
//                        ChatUserInfoFragment().show(childFragmentManager)
                    }

                    ChatMsgType.TEXT -> {
                        ChatPersonalDialogFragment.show(this@ChatPageFragment)
                    }

                    else -> {}
                }
            },
            longClick = { bean ->
                "longClick bean.msgType=${bean.msgType}".logd(TAG)

                if (bean.msgType == ChatMsgType.SYSTEM) {
                    return@ChatPageAdapter
                }
                homeViewModel.addAtMsgToChat(bean)
            }
        )
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
        mBinding.liveChatRecycler.itemAnimator = null
        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN && homeViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
                    homeViewModel.updateKeyBoardUi(KeyBoardType.CHAT, 21)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

    }

    private var refreshJob: Job? = null

    /**
     * 接收到新数据做更新
     * */
    @SuppressLint("NotifyDataSetChanged")
    private fun refreshChatList(flagFlash: Boolean = false) {
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as ChatPageAdapter }
        val nList = mutableListOf<ChatMsgPageBean>()
        nList.addAll(mViewModel.msgLists)
        adapter?.submitList(nList) {

            if (nList.isEmpty()) {
                return@submitList
            }
            if (refreshJob?.isActive == true) {
                refreshJob?.cancel()
            }
            "refreshChat flagFlash $flagFlash".logd(TAG)
            refreshJob = lifecycleScope.launch {
                mBinding.liveChatRecycler.postDelayed({
                    try {
                        mBinding.liveChatRecycler.scrollToPosition(0)
                        mBinding.liveChatRecycler.postDelayed({
                            itemFlash()
                        },500)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, 500)
            }
        }
    }

    override fun initListener() {


    }

    private fun itemFlash() {
        "itemFlash1".logd(TAG)

        val adapter = mBinding.liveChatRecycler.adapter?.let { it as ChatPageAdapter }
        adapter?.currentList?.indexOfFirst { it.flashFlag }?.let { position ->
            "itemFlash position=$position".logd(TAG)
            val holder =
                mBinding.liveChatRecycler.findViewHolderForAdapterPosition(position) as? ChatPageAdapter.LiveChatViewHolder
            holder?.startFlash(position)
        }
    }

    override suspend fun createObserver() {
        homeViewModel.sendMsgLiveData.observe(viewLifecycleOwner) {
            mViewModel.addLocalMsg(it)
            homeViewModel.sendMsgToServer(
                it.content,
                it.refUid,
                ChatType.LOBBY,
                MsgType.getSendMsgType(it.msgType.value),
                it.extraData
            )
            refreshChatList(it.flashFlag)
        }
        viewLifecycleOwner.lifecycleScope.launch {

            launch {
                homeViewModel.chatHistoryFlow.collect {
                    mViewModel.getChatHistory(it?.msgs)
                    refreshChatList()
                }
            }
            launch {
                homeViewModel.registerNewMsgFlow().collect {
                    mViewModel.addNewMsgs(it)
                    refreshChatList(it.msg.refUids?.contains(homeViewModel.myUid) == true)
                }
            }
        }

    }

    private fun listenFragmentResult() {
        //监听个人消息弹窗结果
        childFragmentManager.setFragmentResultListener(
            ChatPersonalDialogFragment.CHAT_PERSONAL_REQUEST,
            this
        ) { key, bundle ->
            val result = bundle.getInt(ChatPersonalDialogFragment.CHAT_PERSONAL_RESULT, -1)
            if (result != -1) {
                //处理结果 0 title 1 @ta 2 复制评论 3 举报评论
                if (result == 1) {
                    selectBean?.let {
                        homeViewModel.addAtMsgToChat(it)
                    }
                }
            }
            val result1 = bundle.getInt(ChatPrivateUserFragment.CHAT_USER_RESULT, -1)
            if (result1 != -1) {
                selectBean?.let {
                    homeViewModel.addAtMsgToChat(it)
                }
            }

        }
    }

    fun clearChatList() {
        mViewModel.clearMsgList()
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as ChatPageAdapter }
        adapter?.submitList(emptyList())
        "clearChatList".logd(TAG)
    }

    companion object {
        val TAG: String = ChatBaseFragment::class.java.simpleName
    }
}