package arch.cayenne.lib.common.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.data.constants.ShareBean
import arch.cayenne.lib.common.databinding.FragmentShareBinding
import arch.cayenne.lib.common.ui.adapter.RecyclerItemListener
import arch.cayenne.lib.common.ui.adapter.ShareAdapter
import arch.cayenne.lib.common.ui.adapter.ShareLinkAdapter
import arch.cayenne.lib.common.ui.viewmodel.ShareViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import kotlin.reflect.KClass

class ShareFragment constructor() :
    BasePreLoadBottomSheetFragment<ShareViewModel, FragmentShareBinding>() {

    companion object {
        val TAG = ShareFragment::class.java.simpleName

        fun create(fragment: Fragment) {
            val manager = fragment.childFragmentManager
            val f = manager.findFragmentByTag(ShareFragment.TAG)
            if (f == null) {
                ShareFragment().customAttach(fragment, ShareFragment.TAG)
            }
        }

        fun show(fragment: Fragment) {
            val f =
                fragment.childFragmentManager.findFragmentByTag(ShareFragment.TAG) as? ShareFragment
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
    }

    override fun initView(savedInstanceState: Bundle?) {
        shareAdapter.submitList(mViewModel.shareApps())
        mBinding.rvShareApps.adapter = shareAdapter
        mBinding.rvShareApps.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        shareAdapter.setItemListener(object : RecyclerItemListener<ShareBean> {
            override fun onItemClick(item: ShareBean?, position: Int) {
                dismiss()
            }
        })
//        mBinding.rvShareLink.adapter = shareLinkAdapter
//       val height =  ImmersionBar.getNavigationBarHeight(mBinding.root.context)

    }


    private fun getNavigationBarHeight(): Int {
        val insets = requireActivity().window.decorView.rootWindowInsets
        return insets.stableInsetBottom
    }

    override fun initListener() {
        val listener: View.OnClickListener = View.OnClickListener { dismiss() }
        mBinding.apply {
            ivClose.setOnClickListener(listener)
            ivBet.setOnClickListener(listener)
            ivLink.setOnClickListener(listener)
            tvLink.setOnClickListener(listener)
            tvBet.setOnClickListener(listener)
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.shareLink.observe(viewLifecycleOwner) {
            shareLinkAdapter.submitList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        val height = getNavigationBarHeight()
        mBinding.root.minHeight = 326.dp2px
    }
}