package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.databinding.FragmentShareBinding
import arch.cayenne.lib.common.ui.adapter.ShareAdapter
import arch.cayenne.lib.common.ui.adapter.ShareLinkAdapter
import arch.cayenne.lib.common.ui.viewmodel.ShareViewModel
import kotlin.reflect.KClass

class ShareFragment private constructor(): BasePreLoadBottomSheetFragment<ShareViewModel, FragmentShareBinding>() {

    companion object {
        val TAG = ShareFragment::class.java.simpleName

        fun create(fragment:Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(ShareFragment.TAG)
            if(f == null){
                ShareFragment().customAttach(fragment,ShareFragment.TAG)
            }
        }

        fun show(fragment: Fragment){
            val f = fragment.childFragmentManager.findFragmentByTag(ShareFragment.TAG) as? ShareFragment
            f?.customShow()

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
        shareAdapter.submitList(mViewModel.shareApps())
        mBinding.rvShareApps.adapter = shareAdapter
        mBinding.rvShareApps.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
//        mBinding.rvShareLink.adapter = shareLinkAdapter
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

    override fun onStart() {
        StatusBarConfig.statusBarType = StatusBarMode.DRAW_BEHIND(autoIsNavigation = true)
        super.onStart()
    }
}