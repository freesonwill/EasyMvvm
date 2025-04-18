package arch.cayenne.module.handicap.ui.fragment

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.handicap.databinding.FragmentSimulateBinding
import arch.cayenne.module.handicap.ui.viewmodel.SimulateViewModel
import kotlin.reflect.KClass
import arch.cayenne.module.handicap.R

/**
 * 模拟投注页面
 */

class SimulateFragment : BaseFragment<SimulateViewModel, FragmentSimulateBinding>() {

    override val vbClass: KClass<FragmentSimulateBinding> = FragmentSimulateBinding::class
    override val vmClass: KClass<SimulateViewModel> = SimulateViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadGeneralTitleBar(R.string.simulate_bet.getString(), null, {
            findNavController().navigateUp()
        })
    }

    override fun initListener() {

    }

    override fun createObserver() {

    }
}