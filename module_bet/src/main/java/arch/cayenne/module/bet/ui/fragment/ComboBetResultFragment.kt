package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.BaseFragment
import arch.cayenne.lib.base.ui.sendResult
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboBetResultBinding
import arch.cayenne.module.bet.viewmodel.ComboBetResultViewModel
import kotlin.reflect.KClass

class ComboBetResultFragment : BaseFragment<ComboBetResultViewModel, FragmentComboBetResultBinding>(),
    BetSheetListener {
    override val vbClass: KClass<FragmentComboBetResultBinding> = FragmentComboBetResultBinding::class
    override val vmClass: KClass<ComboBetResultViewModel> = ComboBetResultViewModel::class

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun initListener() {
        mBinding.tvTest.setOnClickListener {
            dismiss()
        }
    }

    override fun createObserver() {
    }

    override fun dismiss(key: String, value: String) {
        sendResult(key, value, R.id.comboBetResultFragment)
    }
}