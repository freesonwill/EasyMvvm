package arch.cayenne.module.bet.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboDetailBinding
import arch.cayenne.module.bet.ui.adapter.ComboDetailAdapter
import arch.cayenne.module.bet.viewmodel.BetCombViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.reflect.KClass

/**
 * 组合明细弹窗页
 */

class ComboDetailFragment :
    BaseBottomSheetFragment<BetCombViewModel, FragmentComboDetailBinding>() {

    override val vbClass: KClass<FragmentComboDetailBinding> = FragmentComboDetailBinding::class
    override val vmClass: KClass<BetCombViewModel> = BetCombViewModel::class
    private val listAdapter by lazy { ComboDetailAdapter() }
    private var tipStr = ""
    private var isExpand: Boolean = false    //是否已经展开
    private var isCanExpand: Boolean = false //是否可以展开

    companion object {
        private const val PARAMETER = "PARAMETER"
        fun newInstance(parameter: Parameter): ComboDetailFragment {
            return ComboDetailFragment().apply {
                arguments = bundleOf(PARAMETER to GsonUtils.toJson(parameter))
            }
        }
    }

    data class Parameter(
        val title: String,
        val titleTips: String,
        val items: List<ParameterItems>
    )

    data class ParameterItems(
        val title: String,
        val items: List<ParameterItems2>
    )

    data class ParameterItems2(
        val combo: String,
        val money: String?,
        val winMoney: String?,
        val odds: String
    )

    override fun onStart() {
        super.onStart()
        setFitToContents()
    }

    private fun setFitToContents() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
        bottomSheet?.let { sheet ->
            val behavior = BottomSheetBehavior.from(sheet)
            behavior.isDraggable = isVerticalGestureEnable
            behavior.skipCollapsed = isVerticalGestureEnable
            behavior.isHideable = isVerticalGestureEnable
            behavior.isFitToContents = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        val parameter = requireArguments().getString(PARAMETER).let {
            GsonUtils.fromJson(it, Parameter::class.java)
        }
        with(mBinding) {
            tvBetTitle.text = parameter.title
            tipStr = parameter.titleTips
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = listAdapter
        }
        val screenHeight = getScreenHeight() ?: return
        val minHeight = (screenHeight * 0.52).toInt()
        val maxHeight = (screenHeight * 0.84).toInt()
        mBinding.clRoot.maxHeight = maxHeight
        val param = mBinding.clRoot.layoutParams
        if (parameter.items.size > 1) {
            isCanExpand = false
            param.height = maxHeight
        } else {
            isCanExpand = true
            param.height = minHeight
        }
        mBinding.ivBetExpand.setImageResource(arch.cayenne.lib.common.R.drawable.icon_expand)
        mBinding.clRoot.layoutParams = param
        listAdapter.submitList(parameter.items)
    }

    override fun initListener() {
        mBinding.ivBetClose.clickNoRepeat {
            dismiss()
        }
        mBinding.ivBetInfo.clickNoRepeat {
            val location = IntArray(2)
            mBinding.ivBetInfo.getLocationInWindow(location)
            val positionX = location.first() + mBinding.ivBetInfo.width / 2
            val positionY = location.last()
            BetInfoDialogFragment.newInstance(positionX, positionY, tipStr)
                .show(parentFragmentManager)
        }
        mBinding.ivBetExpand.clickNoRepeat {
            onCollapses()
        }
    }

    override suspend fun createObserver() {}

    private fun getScreenHeight(): Int? {
        return context?.resources?.displayMetrics?.heightPixels
    }

    private fun onCollapses() {
        if (!isCanExpand) return
        isExpand = !isExpand
        val screenHeight = getScreenHeight() ?: return
        val max = (screenHeight * 0.84).toInt()
        val min = (screenHeight * 0.52).toInt()
        val start = if (isExpand) min else max
        val end = if (isExpand) max else min
        val layoutParams = mBinding.clRoot.layoutParams
        ValueAnimator.ofInt(start, end).apply {
            duration = 150
            addUpdateListener {
                val value = it.animatedValue as Int
                layoutParams.height = value
                mBinding.clRoot.layoutParams = layoutParams
            }
            addListener(
                onEnd = {
                    if (isExpand) {
                        mBinding.ivBetExpand.setImageResource(arch.cayenne.lib.common.R.drawable.icon_expand_none)
                    } else {
                        mBinding.ivBetExpand.setImageResource(arch.cayenne.lib.common.R.drawable.icon_expand)
                    }
                })
        }.start()
    }
}