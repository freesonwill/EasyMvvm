package arch.cayenne.module.betslip.ui.dialog

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import com.google.android.material.tabs.TabLayout
import kotlin.reflect.KClass
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentEarlySettledNumberKeyboardBinding
import arch.cayenne.module.betslip.ui.viewmodel.EarlySettledKeyboardViewModel


/**
 * 提前结算报价
 * */
class BetSlipEarlySettledFragment private constructor() :
    BaseBottomSheetFragment<EarlySettledKeyboardViewModel, FragmentEarlySettledNumberKeyboardBinding>() {
    private val betIdKey = "bet_id"
    private val betAmountKey = "bet_amount"

    companion object {
        fun instance(
            betId: String,
            money: Double,
        ): BetSlipEarlySettledFragment {
            return BetSlipEarlySettledFragment().apply {
                arguments = Bundle().apply {
                    putString(betIdKey, betId)
                    putDouble(betAmountKey, money)
                }
            }
        }
    }

    override val vbClass: KClass<FragmentEarlySettledNumberKeyboardBinding>
        get() = FragmentEarlySettledNumberKeyboardBinding::class
    override val vmClass: KClass<EarlySettledKeyboardViewModel>
        get() = EarlySettledKeyboardViewModel::class
    private var onEarlySettleClick: ((betId: String, money: Double) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        val betId = arguments?.getString(betIdKey) ?: ""
        val betAmount = arguments?.getDouble(betAmountKey) ?: 0.0
        mViewModel.setArguments(betId, betAmount)

        with(mBinding) {
            initTab(tabLayout = llTab)
            ViewUtils.hideKeyboard(requireContext(), etMoney){v ->
                if (!mBinding.groupKeyboard.isVisible) {
                    mBinding.groupKeyboard.isVisible = true
                }
            }
            etMoney.requestFocus()
            numberKeyboard.setOnCalculatorClickListener(object :
                NumberKeyboardView.OnCalculatorClickListener {
                override fun onNumberClick(number: Int) {
                    mViewModel.addNumber(number)
                }

                override fun onDotClick() {
                    mViewModel.setDot()
                }

                override fun onOtherClick() {
                    mViewModel.setNumber(5000)
                }

                override fun getOtherText(): String {
                    mViewModel.setPercentNumber(1.0)
                    return ContextCompat.getString(requireContext(), R.string.keyboard_max)
                }
            })
            btn100.setOnClickListener { mViewModel.setNumber(100) }
            btn500.setOnClickListener { mViewModel.setNumber(500) }
            btn2000.setOnClickListener { mViewModel.setNumber(2000) }
            btn5000.setOnClickListener { mViewModel.setNumber(5000) }
            btnBack.setOnClickListener { mViewModel.backNumber() }
            btnClear.setOnClickListener { mViewModel.clearNumber() }
            btnDouble.setOnClickListener { mViewModel.doubleNumber() }
            btnCollapse.setOnClickListener {
                mBinding.groupKeyboard.isVisible = false
            }
            btnPartSettle.clickNoRepeat {
                onEarlySettleClick?.invoke(
                    mViewModel.betId,
                    mViewModel.earlySettlePriceLiveData.value ?: 0.0
                )
                dismiss()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    private fun initTab(tabLayout: SkinnableTabLayout) {
        val array = resources.getStringArray(R.array.keyboard_percent)
        array.forEach { text ->
            // 添加新Tab
            val newTab = tabLayout.newTab()
            newTab.text = text
            tabLayout.addTab(newTab)
        }
        reflexPadding(tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val value: Double = when (tab?.position) {
                    0 -> 1.0
                    1 -> 0.25
                    2 -> 0.5
                    3 -> 0.75
                    else -> 0.0
                }
                mViewModel.setPercentNumber(value)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun reflexPadding(tabLayout: TabLayout) {
        tabLayout.post {
            try {
                val mTabStrip = tabLayout.getChildAt(0) as LinearLayout
                for (i in 0 until mTabStrip.childCount) {
                    val tabView = mTabStrip.getChildAt(i)
                    val params = tabView.layoutParams as LinearLayout.LayoutParams
                    params.width = 81.dp2px
                    params.height = 36.dp2px
                    params.marginStart = if (i == 0) 0 else 10.dp2px
                    tabView.layoutParams = params
                    tabView.invalidate()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initListener() {
    }

    fun setOnEarlySettleListener(listener: (betId: String, money: Double) -> Unit) {
        this.onEarlySettleClick = listener
    }

    override fun createObserver() {
        mViewModel.editNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            mBinding.etMoney.setSelection(it.length)
        }
        mViewModel.earlySettlePriceLiveData.observe(viewLifecycleOwner) {
            mBinding.tvBetMoney.text = getString(
                R.string.refund_amount
            ).format(it)
        }
    }
}