package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.databinding.FragmentLoginOrRegisterBinding
import arch.cayenne.module.account.databinding.TitleBarSystemAvatarBinding
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import kotlin.reflect.KClass

class LoginOrRegisterFragment :
    BaseFragment<SystemAvatarViewModel, FragmentLoginOrRegisterBinding>() {
    override val vbClass: KClass<FragmentLoginOrRegisterBinding> =
        FragmentLoginOrRegisterBinding::class
    override val vmClass: KClass<SystemAvatarViewModel> = SystemAvatarViewModel::class
    private val titleBarBinding: TitleBarSystemAvatarBinding by lazy {
        TitleBarSystemAvatarBinding.inflate(LayoutInflater.from(context), mBinding.titleBar, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)
        titleBarBinding.ivBack.clickNoRepeat {
            findNavController().navigateUp()
        }

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {

    }

    override suspend fun createObserver() {

    }

}