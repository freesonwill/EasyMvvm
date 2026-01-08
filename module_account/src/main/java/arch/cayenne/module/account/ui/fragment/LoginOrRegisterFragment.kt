package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.databinding.TitleBarSimpleBinding
import arch.cayenne.lib.common.utils.ext.addScaleOnTouchAnimation
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.account.R
import arch.cayenne.module.account.databinding.FragmentLoginOrRegisterBinding
import arch.cayenne.module.account.ui.viewmodel.LoginOrRegisterViewModel
import arch.cayenne.module.account.ui.viewmodel.SystemAvatarViewModel
import kotlin.reflect.KClass

class LoginOrRegisterFragment :
    BaseFragment<LoginOrRegisterViewModel, FragmentLoginOrRegisterBinding>() {
    override val vbClass: KClass<FragmentLoginOrRegisterBinding> =
        FragmentLoginOrRegisterBinding::class
    override val vmClass: KClass<LoginOrRegisterViewModel> = LoginOrRegisterViewModel::class

    private val titleBarBinding: TitleBarSimpleBinding by lazy {
        TitleBarSimpleBinding.inflate(
            LayoutInflater.from(context),
            mBinding.titleBar,
            false
        )
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.titleBar.loadDynamicsTitleBars(titleBarBinding.root)

        titleBarBinding.apply {
            tvTitleName.text = getString(R.string.title_login_or_register)
        }

    }

    override fun initData() {
        super.initData()
    }

    override fun initListener() {
        with(titleBarBinding) {
            ivBack.addScaleOnTouchAnimation()
            ivBack.clickNoRepeat {
                findNavController().navigateUp()
            }
        }

    }

    override suspend fun createObserver() {

    }

}