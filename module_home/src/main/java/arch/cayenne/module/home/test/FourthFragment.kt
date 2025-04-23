package arch.cayenne.module.home.test

import android.os.Bundle
import arch.cayenne.lib.base.data.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.common.utils.ext.NavResultExt.sendResult
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.FragmentTestFourthBinding
import kotlin.reflect.KClass

class FourthFragment : BaseFragment<EmptyViewModel, FragmentTestFourthBinding>() {
    override val vbClass: KClass<FragmentTestFourthBinding> = FragmentTestFourthBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            sendResult("hello","FourthFragment:${System.currentTimeMillis()}",R.id.secondFragment)
            //navigate(FourthFragmentDirections.actionFourthFragmentToHomeFragment())
        }
    }


    override fun createObserver() {
    }

}