package arch.cayenne.module.bet.ui.fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.module.bet.databinding.FragmentComboDetailBinding
import arch.cayenne.module.bet.ui.adapter.ComboDetailAdapter
import arch.cayenne.module.bet.viewmodel.BetCombViewModel
import com.blankj.utilcode.util.GsonUtils
import kotlin.reflect.KClass

/**
 * 组合明细弹窗页
 */

class ComboDetailFragment :
    BaseBottomSheetFragment<BetCombViewModel, FragmentComboDetailBinding>() {

    override val vbClass: KClass<FragmentComboDetailBinding> =
        FragmentComboDetailBinding::class
    override val vmClass: KClass<BetCombViewModel> = BetCombViewModel::class
    private val listAdapter by lazy { ComboDetailAdapter() }
    private var tipStr = ""
    private var isExpand: Boolean = false //是否展开全屏

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
        if (parameter.items.size > 1) {
            val screenHeight = getScreenHeight() ?: return
            val minFragmentHeight = (screenHeight * 0.86).toInt()
            mBinding.clRoot.minHeight = minFragmentHeight
        } else {
            val screenHeight = getScreenHeight() ?: return
            val minFragmentHeight = (screenHeight * 0.52).toInt()
            mBinding.clRoot.minHeight = minFragmentHeight
        }
        listAdapter.submitList(parameter.items)
    }

    override fun initListener() {
        mBinding.ivBetClose.clickNoRepeat {
            this@ComboDetailFragment.dismiss()
            this@ComboDetailFragment.dialog?.dismiss()
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
            //onCollapses()
        }
    }

    override suspend fun createObserver() {}

    private fun getScreenHeight(): Int? {
        return context?.resources?.displayMetrics?.heightPixels
    }
}