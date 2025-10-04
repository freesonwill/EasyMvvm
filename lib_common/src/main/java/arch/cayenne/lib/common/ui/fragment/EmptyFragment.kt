package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.databinding.FragmentEmptyBinding
import kotlin.reflect.KClass

/**
 * @date: 2025/10/4 14:47
 * @description:
 */
class EmptyFragment : BaseFragment<EmptyViewModel, FragmentEmptyBinding>() {
    override val vbClass: KClass<FragmentEmptyBinding>
        get() = FragmentEmptyBinding::class
    override val vmClass: KClass<EmptyViewModel>
        get() = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
    }

    override suspend fun createObserver() {
    }
}