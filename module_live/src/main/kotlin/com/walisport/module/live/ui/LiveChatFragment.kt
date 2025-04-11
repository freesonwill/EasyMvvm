package com.walisport.module.live.ui

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.viewBind
import com.walisport.module.live.R
import com.walisport.module.live.data.EmojiEnum
import com.walisport.module.live.data.model.LiveChatBean
import com.walisport.module.live.databinding.FragmentLiveBetslipUnsettledBinding
import com.walisport.module.live.databinding.FragmentLiveChatBinding
import com.walisport.module.live.ui.adapter.LiveChatAdapter
import com.walisport.module.live.ui.viewmodel.LiveBetSlipUnsettledViewModel
import com.walisport.module.live.ui.viewmodel.LiveChatViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

//聊天
class LiveChatFragment : BaseFragment<LiveChatViewModel, FragmentLiveChatBinding>() {
    override val vbClass: KClass<FragmentLiveChatBinding> = FragmentLiveChatBinding::class
    override val vmClass: KClass<LiveChatViewModel> = LiveChatViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        val layoutManger = LinearLayoutManager(context)
        val adapter = LiveChatAdapter()
        adapter.submitList(testData())
        mBinding.liveChatRecycler.layoutManager = layoutManger
        mBinding.liveChatRecycler.adapter = adapter

    }

    override fun initListener() {
    }

    override fun createObserver() {
    }

    private fun testData(): List<LiveChatBean> {

        return arrayListOf(
            LiveChatBean("博弈卡卡卡卡：", "此处是玩家对主播的聊天"),
            LiveChatBean("人生戏剧化", "阿根廷加油${EmojiEnum.Emm.key}"),
            LiveChatBean("范大厨美食", "支持姆总监夺冠${EmojiEnum.Angry.key}"),
            LiveChatBean("范大厨美食", "${EmojiEnum.Bye.key}${EmojiEnum.Awesome.key}${EmojiEnum.BahR.key}"),
            LiveChatBean(
                "博弈卡卡卡卡：",
                "两行示意两行示意两行示意两行示意意两行示意意两行示意意两行示意意两行示意"
            ),
            LiveChatBean(
                "博弈卡卡卡卡",
                "决赛好刺激${EmojiEnum.Beer.key}${EmojiEnum.Beer.key}${EmojiEnum.Beer.key}"
            ),
            LiveChatBean(
                "博弈卡卡卡卡",
                "决赛好刺激${EmojiEnum.Goal.key}"
            ),
        )
    }


}