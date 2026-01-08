package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.account.databinding.FragmentEmailBinding
import arch.cayenne.module.account.databinding.FragmentPhoneBinding
import arch.cayenne.module.account.ui.viewmodel.EmailViewModel
import arch.cayenne.module.account.ui.viewmodel.PhoneViewModel
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import kotlin.reflect.KClass

class EmailFragment :
    BaseFragment<EmailViewModel, FragmentEmailBinding>() {
    override val vbClass: KClass<FragmentEmailBinding> =
        FragmentEmailBinding::class
    override val vmClass: KClass<EmailViewModel> = EmailViewModel::class


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