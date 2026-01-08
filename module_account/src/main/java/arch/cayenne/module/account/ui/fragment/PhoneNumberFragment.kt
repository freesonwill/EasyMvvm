package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.databinding.FragmentPhoneBinding
import arch.cayenne.module.account.ui.viewmodel.PhoneViewModel
import kotlin.reflect.KClass

class PhoneNumberFragment :
    BaseFragment<PhoneViewModel, FragmentPhoneBinding>() {
    override val vbClass: KClass<FragmentPhoneBinding> =
        FragmentPhoneBinding::class
    override val vmClass: KClass<PhoneViewModel> = PhoneViewModel::class


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            tvCountryCode.text = "+86" //先做*86
        }

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {
        mBinding.llWrapper.clickNoRepeat {
            //TODO 选择国家地区
        }

    }

    override suspend fun createObserver() {

    }

}