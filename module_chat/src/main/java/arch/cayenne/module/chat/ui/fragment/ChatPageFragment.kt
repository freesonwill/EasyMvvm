package arch.cayenne.module.chat.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.loge
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.lib.websocket.chat.data.ChatMsg
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.databinding.FragementChatPageLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatAdapter
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
        ChatPersonalDialogFragment.create(this)
        initRecycler()

    }

    private fun initRecycler(){
        val layoutManger = LinearLayoutManager(context).apply {
            orientation = LinearLayoutManager.VERTICAL
            reverseLayout = true
        }
        val adapter = ChatAdapter()

        adapter.setOnItemListener(object :RecyclerItemListener<ChatMsg>{
            override fun onItemClick(item: ChatMsg?, position: Int) {
                "show item".loge("aaa")
                ChatPersonalDialogFragment.show(this@ChatPageFragment)
            }
        })

        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
        mBinding.liveChatRecycler.itemAnimator = null
        mBinding.liveChatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if(e.action == MotionEvent.ACTION_DOWN && homeViewModel.currentKeyBoardType != KeyBoardType.CHAT){
                    homeViewModel.updateKeyBoardUi(KeyBoardType.CHAT,21)
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })

    }

    /**
     * 接收到新数据做更新
     * */
    @SuppressLint("NotifyDataSetChanged")
    private fun refreshChatList() {
        val adapter = mBinding.liveChatRecycler.adapter?.let { it as ChatAdapter }
        val nList = mutableListOf<ChatMsg>()
        nList.addAll(mViewModel.msgLists)
        adapter?.submitList(nList)
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