package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.databinding.FragmentShareBinding
import arch.cayenne.lib.common.ui.adapter.ShareAdapter
import arch.cayenne.lib.common.ui.adapter.ShareLinkAdapter
import arch.cayenne.lib.common.ui.viewmodel.ShareViewModel
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

    private val shareLinkAdapter: ShareLinkAdapter by lazy {
        ShareLinkAdapter()
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvShareApps.adapter = shareAdapter
        mBinding.rvShareLink.adapter = shareLinkAdapter
    }

    override fun initListener() {
        mBinding.ivClose.setOnClickListener {
            dismiss()
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.shareLink.observe(viewLifecycleOwner) {
            shareLinkAdapter.submitList(it)
        }
    }
}