package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.account.databinding.FragmentPhoneBinding
import arch.cayenne.module.account.ui.viewmodel.PhoneViewModel
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import kotlin.reflect.KClass

class EmailFragment :
    BaseFragment<PhoneViewModel, FragmentPhoneBinding>() {
    override val vbClass: KClass<FragmentPhoneBinding> =
        FragmentPhoneBinding::class
    override val vmClass: KClass<PhoneViewModel> = PhoneViewModel::class


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {


    }

    override suspend fun createObserver() {

    }

}