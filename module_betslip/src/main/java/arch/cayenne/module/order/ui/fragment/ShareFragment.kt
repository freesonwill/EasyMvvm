package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.databinding.FragmentShareBinding
import arch.cayenne.module.order.ui.adapter.ShareAdapter
import arch.cayenne.module.order.ui.viewmodel.ShareViewModel
import kotlin.reflect.KClass

class ShareFragment private constructor(): BaseBottomSheetFragment<ShareViewModel, FragmentShareBinding>() {

    companion object {
        fun newInstance(): ShareFragment {
            return ShareFragment()
        }
    }

    override val vbClass: KClass<FragmentShareBinding> = FragmentShareBinding::class
    override val vmClass: KClass<ShareViewModel> = ShareViewModel::class

    private val shareAdapter: ShareAdapter by lazy {
        ShareAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvShareApps.adapter = shareAdapter
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }
}