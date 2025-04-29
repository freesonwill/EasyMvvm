package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.module.home.databinding.FragmentDrawerCommonFunctionBinding
import kotlin.reflect.KClass

class DrawerCommonFunctionFragment: BaseFragment<EmptyViewModel, FragmentDrawerCommonFunctionBinding>() {
    override val vbClass: KClass<FragmentDrawerCommonFunctionBinding> = FragmentDrawerCommonFunctionBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}