package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.module.home.databinding.FragmentDrawerBottomBinding
import kotlin.reflect.KClass

class DrawerBottomFragment: BaseFragment<EmptyViewModel, FragmentDrawerBottomBinding>() {
    override val vbClass: KClass<FragmentDrawerBottomBinding> = FragmentDrawerBottomBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}