package arch.cayenne.module.home.test

import android.os.Bundle
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResult
import arch.cayenne.lib.common.utils.ext.NavResultExt.observeResultOnce
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.module.home.databinding.FragmentTestSecondBinding
import kotlin.reflect.KClass


class SecondFragment : BaseFragment<EmptyViewModel, FragmentTestSecondBinding>() {
    override val vbClass: KClass<FragmentTestSecondBinding> = FragmentTestSecondBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        "observeResult--->hello-->".logd(TAG)
        observeResult<String>("hello"){
            "observeResult--->hello-->$it".logd(TAG)
            mBinding.tv.text = "observeResult$it"
        }
        observeResultOnce<String>("hello") {
            mBinding.tv.text = "observeResultOnce$it"
            "observeResultOnce--->hello-->$it".logd(TAG)
        }
    }

    override fun initListener() {
        mBinding.root.setOnClickListener {
            "setOnClickListener--->".logd(TAG)
            navigate(SecondFragmentDirections.actionSecondFragmentToThirdFragment())
        }
    }

    override fun createObserver() {
    }

}