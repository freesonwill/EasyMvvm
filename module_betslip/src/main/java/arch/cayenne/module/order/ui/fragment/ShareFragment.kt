package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.databinding.FragmentShareBinding
import arch.cayenne.module.order.ui.viewmodel.ShareViewModel
import kotlin.reflect.KClass

class ShareFragment : BaseBottomSheetFragment<ShareViewModel, FragmentShareBinding>() {

    override val vbClass: KClass<FragmentShareBinding> = FragmentShareBinding::class
    override val vmClass: KClass<ShareViewModel> = ShareViewModel::class

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}