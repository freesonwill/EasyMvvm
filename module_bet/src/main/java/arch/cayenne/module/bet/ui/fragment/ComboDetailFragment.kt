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
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDimensionPixelSize
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getOdds
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.R
import arch.cayenne.module.bet.databinding.FragmentComboDetailBinding
import arch.cayenne.module.bet.ui.adapter.ComboDetailAdapter
import arch.cayenne.module.bet.util.BetUtils
import arch.cayenne.module.bet.util.BetUtils.isSuperCombo
import arch.cayenne.module.bet.viewmodel.BetCombViewModel
import com.blankj.utilcode.util.GsonUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import me.jessyan.autosize.utils.ScreenUtils
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


    companion object {
        private const val PARAMETER = "PARAMETER"
        fun newInstance(parameter: Parameter): ComboDetailFragment {
            return ComboDetailFragment().apply {
                arguments = bundleOf(PARAMETER to GsonUtils.toJson(parameter))
            }
        }
    }

    data class Parameter(
        val serialValue: Int,
        val comboK: Int,
        val comboV:Int,
        val items: List<ParameterItems>
    )

    data class ParameterItems(
        val title: String,
        val items: List<ParameterItems2>
    )

    data class ParameterItems2(
        val combo: List<Int>,
        val money: Long,
        val oddsList:List<Int>,
        val moneySymbol:String,
    ){
        val comboStr:String get() = combo.joinToString("·") { "${it + 1}" }
        val oddsStr:String get() = "@${odds.getOdds(false)}"
        val winMoneyStr: String? get() = money.takeIf { it != 0L }?.let { "$moneySymbol${money.getMoney(odds,false)}" }
        val moneyStr:String? get() = money.takeIf { it != 0L }?.let { "$moneySymbol${it.getMoney(false)}" }
        val odds: Int get() = BetUtils.calculateCombinationOdds(oddsList,oddsList.size)
    }

    data class ParameterUI(
        val serialValue: Int,
        val comboK: Int,
        val comboV:Int,
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
                return ParameterUI(data.serialValue,data.comboK,data.comboV,result)
            }
        }

        fun title():String {
            return when {
                isSuperCombo(serialValue) -> R.string.title_combo_bet_super.getString()
                else -> R.string.title_combo_bet_tittle.getString(comboK,comboV)
            }
        }

        fun titleTips():String{
            return when {
                comboV == 1 -> {
                    R.string.title_combo_bet_detail_tips.getString(
                        title(),
                        arch.cayenne.lib.res.R.string.title_combo_bet_odds.getString(comboK,comboV)
                    )
                }
                else ->
                    R.string.title_combo_bet_detail_tips.getString(
                        title(),
                        ((if(isSuperCombo(serialValue)) 1 else 2)..comboK).joinToString("、") { k ->
                            if (k == 1) arch.cayenne.lib.res.R.string.title_single_bet.getString()
                            else arch.cayenne.lib.common.R.string.title_combo_bet.getString(k, 1)
                        }
                    )
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
        val parameter = requireArguments().getString(PARAMETER).let {
            GsonUtils.fromJson(it, Parameter::class.java)
        }
        val data = ParameterUI.buildParameterUI(parameter)
        with(mBinding) {
            tvBetTitle.text = data.title()
            tipStr = data.titleTips()
            rvContent.layoutManager = LinearLayoutManager(requireContext())
            rvContent.adapter = listAdapter
        }
        val screenHeight = getScreenHeight()
        val minHeight = (screenHeight * 425f/812).toInt()
        val maxHeight = (screenHeight * 699f/812).toInt()

        mBinding.clRoot.maxHeight = maxHeight
        mBinding.clRoot.layoutParams = mBinding.clRoot.layoutParams.apply {
            if (data.items.size > 12) {
                height = maxHeight
                mViewModel.isExpand = BetCombViewModel.ExpandState.EXPANDED
            } else {
                height = minHeight
                mViewModel.isExpand = BetCombViewModel.ExpandState.COLLAPSED
            }
        }
        listAdapter.submitList(data.items)
    }

    override fun isDimControllerEnabled(): Boolean {
        return false
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
            toggleExpandOrCollapse()
        }
    }

    override suspend fun createObserver() {
        launch {
            mViewModel.isExpandFlow.collect {
                when(it){
                    BetCombViewModel.ExpandState.EXPANDED -> {
                        mBinding.ivBetExpand.setImageResource(arch.cayenne.lib.common.R.drawable.icon_expand_none)
                    }
                    BetCombViewModel.ExpandState.COLLAPSED -> {
                        mBinding.ivBetExpand.setImageResource(arch.cayenne.lib.common.R.drawable.icon_expand)
                    }
                    else -> Unit
                }

            }
        }
    }

    private fun getScreenHeight(): Int {
        return ScreenUtils.getScreenSize(requireContext())[1]
    }

    private fun toggleExpandOrCollapse() {
        if(mViewModel.isExpand == BetCombViewModel.ExpandState.EXPANDING
            || mViewModel.isExpand == BetCombViewModel.ExpandState.COLLAPSING){
            return
        }

        val endState = if(mViewModel.isExpand == BetCombViewModel.ExpandState.EXPANDED){
            mViewModel.isExpand = BetCombViewModel.ExpandState.COLLAPSING
            BetCombViewModel.ExpandState.COLLAPSED
        }
        else{
            mViewModel.isExpand = BetCombViewModel.ExpandState.EXPANDING
            BetCombViewModel.ExpandState.EXPANDED
        }
        val screenHeight = getScreenHeight()
        val min = (screenHeight * 425f/812).toInt()
        val max = (screenHeight * 699f/812).toInt()
        val start = if (mViewModel.isExpand == BetCombViewModel.ExpandState.EXPANDING) min else max
        val end = if (mViewModel.isExpand == BetCombViewModel.ExpandState.EXPANDING) max else min
        val layoutParams = mBinding.clRoot.layoutParams
        ValueAnimator.ofInt(start, end).apply {
            duration = 150
            addUpdateListener {
                val value = it.animatedValue as Int
                layoutParams.height = value
                mBinding.clRoot.layoutParams = layoutParams
            }
            addListener(onEnd = {
                mViewModel.isExpand = endState
            })
        }.start()
    }

}