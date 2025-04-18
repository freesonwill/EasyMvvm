package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.handicap.databinding.FragmentHandicapBinding
import arch.cayenne.module.handicap.ui.viewmodel.HandicapViewModel
import kotlin.reflect.KClass
import arch.cayenne.module.handicap.R

/**
 * 盘口教程页面
 */

class HandicapFragment : BaseFragment<HandicapViewModel, FragmentHandicapBinding>() {

    override val vbClass: KClass<FragmentHandicapBinding> = FragmentHandicapBinding::class
    override val vmClass: KClass<HandicapViewModel> = HandicapViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(
            R.string.handicap_lesson.getString(),
            { findNavController().navigateUp() },
            { navigate(HandicapFragmentDirections.actionHandicapFragmentToSimulateFragment()) },
            R.string.simulate_bet.getString()
        )
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}