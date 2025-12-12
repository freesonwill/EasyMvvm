package arch.cayenne.module.bet.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.base.ui.fragment.launch
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.databinding.FragmentComboDetailBinding
import arch.cayenne.module.bet.ui.adapter.ComboDetailAdapter
import arch.cayenne.module.bet.viewmodel.BetCombViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    data class ParameterUI(
        val title: String,
        val titleTips: String,
        val items:List<ParameterUIItem>
    ){
        companion object {
            const val TYPE_GROUP = 1        // ParameterItems
            const val TYPE_CHILD = 2        // ParameterItems2

            //将一层数据摊平层两层数据
            fun buildParameterUI(data: Parameter): ParameterUI {
                val result = mutableListOf<ParameterUIItem>()
                data.items.forEach { group ->
                    // 第二层：组标题
                    result.add(ParameterUIItem(type = TYPE_GROUP, group = group))

                    // 第三层：组内内容
                    group.items.forEachIndexed { index, child ->
                        result.add(ParameterUIItem(type = TYPE_CHILD, child = child,childIndexInGroup = index))
                    }
                }
                return ParameterUI(data.title,data.titleTips,result)
            }
        }
    }

    data class ParameterUIItem(
        val type: Int,
        val group: ParameterItems? = null,
        val child: ParameterItems2? = null,
        val childIndexInGroup: Int = -1 // 记录 child 在 group 内的原始 index
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
        launch {
            val parameter = withContext(Dispatchers.IO){
                requireArguments().getString(PARAMETER).let {
                    GsonUtils.fromJson(it, Parameter::class.java)
                }
            }
            val data = withContext(Dispatchers.IO){
                ParameterUI.buildParameterUI(parameter)
            }
            with(mBinding) {
                tvBetTitle.text = data.title
                tipStr = data.titleTips
                rvContent.layoutManager = LinearLayoutManager(requireContext())
                rvContent.adapter = listAdapter
            }
            val screenHeight = getScreenHeight()
            val minHeight = (screenHeight * 0.52).toInt()
            val maxHeight = (screenHeight * 0.84).toInt()
            mBinding.clRoot.maxHeight = maxHeight
            mBinding.clRoot.layoutParams = mBinding.clRoot.layoutParams.apply {
                if (parameter.items.size > 1) {
                    isCanExpand = false
                    height = maxHeight
                } else {
                    isCanExpand = true
                    height = minHeight
                }
            }
            listAdapter.submitList(data.items)
        }
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

    private fun getScreenHeight(): Int {
        return requireContext().resources.displayMetrics.heightPixels
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