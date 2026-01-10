package arch.cayenne.module.account.ui.fragment

import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.account.databinding.FragmentEmailBinding
import arch.cayenne.module.account.ui.viewmodel.EmailViewModel
import kotlin.reflect.KClass

class EmailFragment :
    BaseFragment<EmailViewModel, FragmentEmailBinding>() {
    override val vbClass: KClass<FragmentEmailBinding> =
        FragmentEmailBinding::class
    override val vmClass: KClass<EmailViewModel> = EmailViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.llNextWrapper.isEnabled = false
        mBinding.llNextWrapper.alpha = 0.4f
        mBinding.editTextEmail.addTextChangedListener {
            mBinding.llNextWrapper.isEnabled =
                (mBinding.editTextEmail.text?.length ?: 0) > 0
            mBinding.llNextWrapper.alpha =
                if (mBinding.llNextWrapper.isEnabled) 1.0f else 0.4f
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