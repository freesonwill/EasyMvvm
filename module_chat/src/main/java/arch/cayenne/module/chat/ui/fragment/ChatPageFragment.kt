package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.databinding.FragementChatPageLayoutBinding
import arch.cayenne.module.chat.ui.adapter.LiveChatAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatHomeViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatPageViewModel
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 1/10/25 15:09
 * @description:
 */
class ChatPageFragment:BaseFragment<ChatPageViewModel,FragementChatPageLayoutBinding>() {
    override val vbClass: KClass<FragementChatPageLayoutBinding>
        get() = FragementChatPageLayoutBinding::class
    override val vmClass: KClass<ChatPageViewModel>
        get() = ChatPageViewModel::class
    private val homeViewModel:ChatHomeViewModel by sharedViewModel<ChatHomeViewModel,ChatHomeFragment>()

    override fun initView(savedInstanceState: Bundle?) {
        initRecycler()
    }

    private fun initRecycler(){
        val layoutManger = LinearLayoutManager(context)
        val adapter = LiveChatAdapter()
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
        mBinding.liveChatRecycler.itemAnimator = null
//        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                if (e.action == MotionEvent.ACTION_UP && mViewModel.currentKeyBoardType != KeyBoardType.CHAT) {
//                    showChat(7)
//                }
//                return false
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//            }
//        })

    }

    /**
     * 接收到新数据做更新
     * */
    private fun refreshChatList() {
        homeViewModel.refreshChatUi(mViewModel.msgLists.isEmpty())
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as LiveChatAdapter }
        adapter?.submitList(mViewModel.msgLists) {
            adapter.currentList.size.let {
                val position = it - 1
                if (position >= 0) {
                    mBinding.liveChatRecycler.scrollToPosition(position)
                }
            }
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
        homeViewModel.sendMsgLiveData.observe(viewLifecycleOwner) {
            mViewModel.addLocalMsg(homeViewModel.addLocalMsg(it))
            homeViewModel.sendMsgToServer(it)
            refreshChatList()
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
                    refreshChatList()
                }
            }
        }

    }

    companion object{
         val TAG: String = ChatHomeFragment::class.java.simpleName
    }
}