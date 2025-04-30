package arch.cayenne.module.home.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.module.home.databinding.FragmentDrawerNotificationBinding
import kotlin.reflect.KClass

class DrawerNotificationFragment : BaseFragment<EmptyViewModel, FragmentDrawerNotificationBinding>() {
    override val vbClass: KClass<FragmentDrawerNotificationBinding> = FragmentDrawerNotificationBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class
    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}