package arch.cayenne.module.launcher.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.launcher.databinding.FragmentLauncherBinding
import arch.cayenne.module.launcher.ui.viewmodel.LauncherViewModel
import kotlin.reflect.KClass

class LauncherFragment: BaseFragment<LauncherViewModel, FragmentLauncherBinding>() {
    override val vbClass: KClass<FragmentLauncherBinding> = FragmentLauncherBinding::class
    override val vmClass: KClass<LauncherViewModel> = LauncherViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }
}