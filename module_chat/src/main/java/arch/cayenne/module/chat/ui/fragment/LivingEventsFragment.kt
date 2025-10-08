package arch.cayenne.module.chat.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.chat.databinding.FragmentLivingEventsLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.LivingEventsViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 7/10/25 11:13
 * @description: 赛事直播
 */
class LivingEventsFragment :BaseFragment<LivingEventsViewModel,FragmentLivingEventsLayoutBinding>(){
    override val vbClass: KClass<FragmentLivingEventsLayoutBinding>
        get() = FragmentLivingEventsLayoutBinding::class
    override val vmClass: KClass<LivingEventsViewModel>
        get() = LivingEventsViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}