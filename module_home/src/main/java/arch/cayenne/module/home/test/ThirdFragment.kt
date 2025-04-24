package arch.cayenne.module.home.test

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigateUp
import arch.cayenne.module.home.databinding.FragmentTestThirdBinding
import kotlin.reflect.KClass


class ThirdFragment : BaseFragment<EmptyViewModel, FragmentTestThirdBinding>() {
    override val vbClass: KClass<FragmentTestThirdBinding> = FragmentTestThirdBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            sendResult("hello","ThirdFragment:${System.currentTimeMillis()}")
            navigateUp()
            //navigate(ThirdFragmentDirections.actionThirdFragmentToFourthFragment())
        }
    }


    override fun createObserver() {
    }

}