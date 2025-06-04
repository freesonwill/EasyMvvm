package com.walisport.module.live.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.walisport.module.live.data.constants.BidEmojiEnum
import com.walisport.module.live.data.constants.EmojiEnum
import com.walisport.module.live.data.model.LiveChatBean
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>(),
    LiveSoftKeyboardFragment.LiveChatSoftKeyListener {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<LiveChatViewModel> = LiveChatViewModel::class

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initFragment()
        initTab()
    }

    private fun initTab() {
        val layoutManger = LinearLayoutManager(context)
        val adapter = LiveChatAdapter()
        adapter.submitList(testData())
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter
    }


    override fun initListener() {
        mBinding.main.clickNoRepeat {
            showChat()
        }
        mBinding.liveChatRecycler.clickNoRepeat {
            showChat()
        }
    }

    private fun showChat() {
        childFragmentManager.findFragmentByTag(LiveSoftKeyboardFragment.TAG)?.let {
            val keyboardFragment = it as LiveSoftKeyboardFragment
            keyboardFragment.showChat()
        }
    }

    private fun initFragment() {
        val fragment = LiveSoftKeyboardFragment()
        fragment.setSoftKeyListener(this)
        childFragmentManager.beginTransaction()
            .replace(mBinding.liveChatKeyboard.id, fragment, LiveSoftKeyboardFragment.TAG)
            .commit()
    }

    override fun createObserver() {
    }

    private fun testData(): List<LiveChatBean> {

        return arrayListOf(
            LiveChatBean("博弈卡卡卡卡：", "此处是玩家对主播的聊天"),
            LiveChatBean("人生戏剧化", "阿根廷加油${EmojiEnum.Gin.key}"),
            LiveChatBean("范大厨美食", "支持姆总监夺冠${EmojiEnum.Angry.key}"),
            LiveChatBean(
                "范大厨美食",
                "${EmojiEnum.Boring.key}${EmojiEnum.HandHeart.key}${EmojiEnum.Laugh.key}"
            ),
            LiveChatBean(
                "博弈卡卡卡卡：",
                "两行示意两行示意两行示意两行示意意两行示意意两行示意意两行示意意两行示意"
            ),
            LiveChatBean(
                "博弈卡卡卡卡",
                "决赛好刺激${EmojiEnum.Salute.key}${EmojiEnum.Respect.key}${EmojiEnum.Tongue.key}"
            ),
            LiveChatBean(
                "博弈卡卡卡卡",
                "决赛好刺激${BidEmojiEnum.Goal.key}"
            ),
        )
    }

    override fun showKeyBoard() {
//        mBinding.liveChatGroupChat.isVisible = false
    }

    override fun hideKeyboard() {
//        mBinding.liveChatGroupChat.isVisible = true
    }

    override fun onStart() {
        super.onStart()
        mViewModel.chatLogin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }


}