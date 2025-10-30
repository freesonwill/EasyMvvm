package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.listenAtTop
import arch.cayenne.lib.common.utils.ext.sharedViewModel
import arch.cayenne.module.chat.ui.viewmodel.ChatViewPagerViewModel
import arch.cayenne.module.chat.databinding.FragmentChatViewPagerBinding
import arch.cayenne.module.chat.ui.adapter.ChatUserGameAdapter
import arch.cayenne.module.chat.ui.viewmodel.ChatUserInfoViewModel
import kotlin.reflect.KClass

class ChatViewPagerFragment : BaseFragment<ChatViewPagerViewModel,FragmentChatViewPagerBinding>() {

    override val vbClass: KClass<FragmentChatViewPagerBinding>
        get() = FragmentChatViewPagerBinding::class
    override val vmClass: KClass<ChatViewPagerViewModel>
        get() = ChatViewPagerViewModel::class
    private val userInfoViewModel: ChatUserInfoViewModel by sharedViewModel<ChatUserInfoViewModel, ChatUserInfoFragment>()
    private  lateinit var mAdapter:ChatUserGameAdapter
    override fun initView(savedInstanceState: Bundle?) {
        initAdapter()
        mViewModel.getGameList()
    }

    private fun initAdapter() {
        mBinding.rvGameList.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(
                this@ChatViewPagerFragment.context, LinearLayoutManager.VERTICAL, false
            )
            mAdapter = ChatUserGameAdapter()
            adapter = mAdapter
        }

        // 监听 RecyclerView 是否滑动到第一条
        mBinding.rvGameList.listenAtTop { isAtTop ->
            if (isAtTop) {
                userInfoViewModel.setSonVerticalScrollIsTop(true)
            }else{
                userInfoViewModel.setSonVerticalScrollIsTop(false)
            }
        }
    }
    override fun initListener() {

    }

    override suspend fun createObserver() {
        mViewModel.gameList.observe(viewLifecycleOwner){
            mAdapter.submitList(it)
        }
    }


}