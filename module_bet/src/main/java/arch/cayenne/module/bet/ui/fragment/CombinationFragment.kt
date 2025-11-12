package arch.cayenne.module.bet.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentBetCombinationBinding
import arch.cayenne.module.bet.ui.adapter.CombThreeListAdapter
import arch.cayenne.module.bet.ui.adapter.CombinationAdapter
import arch.cayenne.module.bet.viewmodel.BetCombViewModel
import kotlin.reflect.KClass

/**
 * 组合列表弹窗页
 */

class CombinationFragment :
    BasePreLoadBottomSheetFragment<BetCombViewModel, FragmentBetCombinationBinding>() {

    override val vbClass: KClass<FragmentBetCombinationBinding> =
        FragmentBetCombinationBinding::class
    override val vmClass: KClass<BetCombViewModel> = BetCombViewModel::class
    private val headerAdapter by lazy { CombinationAdapter(TYPE_SINGLE) }
    private val listAdapter by lazy { CombThreeListAdapter(TYPE_SINGLE) }
    private val twoHeaderAdapter by lazy { CombinationAdapter(TYPE_TWO) }
    private val doubleAdapter by lazy { CombThreeListAdapter(TYPE_TWO) }

    companion object {
        private const val TAG = "CombinationFragment"
        private const val TYPE_SUPER = 0 //超级组合
        private const val TYPE_SINGLE = 1 //所有单关注单
        private const val TYPE_TWO = 2 //所有2串1注单
        private const val TYPE_THREE = 3 //所有3串1注单

        fun create(activity: FragmentActivity) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                CombinationFragment().customAttach(activity, TAG)
            }
        }

        fun show(activity: FragmentActivity, withOtherSheetHide: ObjectAnimator? = null) {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            if (f == null) {
                CombinationFragment().show(manager, TAG)
            } else if (f is BasePreLoadBottomSheetFragment<*, *>) {
                if (withOtherSheetHide == null) {
                    f.customShow()
                } else {
                    f.customShow(withOtherSheetHide)
                }
            }
        }

        fun find(activity: FragmentActivity): CombinationFragment? {
            val manager = activity.supportFragmentManager
            val f = manager.findFragmentByTag(TAG)
            return f as? CombinationFragment
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            val concatAdapter =
                ConcatAdapter(headerAdapter, listAdapter, twoHeaderAdapter, doubleAdapter)
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = concatAdapter
        }
        val screenHeight = getScreenHeight() ?: return
        val maxFragmentHeight = (screenHeight * 0.86).toInt()
        mBinding.root.maxHeight = maxFragmentHeight
    }

    override fun initListener() {
        mBinding.ivBetClose.clickNoRepeat {
            this@CombinationFragment.dismiss()
            this@CombinationFragment.dialog?.dismiss()
        }
        mBinding.ivBetInfo.clickNoRepeat {
            val location = IntArray(2)
            mBinding.ivBetInfo.getLocationInWindow(location)
            val positionX = location.first() + mBinding.ivBetInfo.width / 2
            val positionY = location.last() - 2 * mBinding.ivBetInfo.height
            BetInfoDialogFragment.newInstance(
                positionX, positionY, R.string.toast_super_bet_info.getString()
            ).show(parentFragmentManager)
        }
        mBinding.ivBetExpand.clickNoRepeat {

        }
    }

    override suspend fun createObserver() {
        mViewModel.onCombinationListener.observe(viewLifecycleOwner) {
            if (it != null) {
                listAdapter.submitList(it)
            }
        }
        mViewModel.onTwoListener.observe(viewLifecycleOwner) {
            if (it != null) {
                doubleAdapter.submitList(it)
            }
        }
    }

    override fun initData() {
        super.initData()
        mViewModel.getSingleData()
        mViewModel.getTwoData()
    }

    private fun getScreenHeight(): Int? {
        return context?.resources?.displayMetrics?.heightPixels
    }
}