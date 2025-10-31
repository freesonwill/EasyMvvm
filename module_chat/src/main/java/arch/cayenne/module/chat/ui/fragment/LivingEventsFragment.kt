package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.databinding.FragmentLivingEventsLayoutBinding
import arch.cayenne.module.chat.ui.adapter.ChatLivingAdapter
import arch.cayenne.module.chat.ui.viewmodel.LivingEventsViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 7/10/25 11:13
 * @description: 赛事直播
 */
class LivingEventsFragment :
    BaseFragment<LivingEventsViewModel, FragmentLivingEventsLayoutBinding>() {
    override val vbClass: KClass<FragmentLivingEventsLayoutBinding>
        get() = FragmentLivingEventsLayoutBinding::class
    override val vmClass: KClass<LivingEventsViewModel>
        get() = LivingEventsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            val nAdapter = ChatLivingAdapter()
            nAdapter.submitList(arrayListOf(1, 2, 3, 4, 5, 6))
            adapter = nAdapter
        }
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}