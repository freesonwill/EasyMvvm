package arch.cayenne.module.betslip.ui.dialog

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoneyForScale
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentEarlySettledNumberKeyboardBinding
import arch.cayenne.module.betslip.ui.viewmodel.EarlySettledKeyboardViewModel
import com.google.android.material.tabs.TabLayout
import kotlin.reflect.KClass


/**
 * 提前结算报价
 * */
class BetSlipEarlySettledFragment private constructor() :
    BaseBottomSheetFragment<EarlySettledKeyboardViewModel, FragmentEarlySettledNumberKeyboardBinding>() {

    companion object {
        private const val BET_AMOUNT_MONEY = "bet_amount_money"

        fun instance(
            money: String,
        ): BetSlipEarlySettledFragment {
            return BetSlipEarlySettledFragment().apply {
                arguments = Bundle().apply {
                    putString(BET_AMOUNT_MONEY, money)
                }
            }
        }
    }

    override val vbClass: KClass<FragmentEarlySettledNumberKeyboardBinding>
        get() = FragmentEarlySettledNumberKeyboardBinding::class
    override val vmClass: KClass<EarlySettledKeyboardViewModel>
        get() = EarlySettledKeyboardViewModel::class
    private var onEarlySettleClick: ((money: String) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {

        with(mBinding) {
            initTab(tabLayout = llTab)
            ViewUtils.hideKeyboard(requireContext(), etMoney) { _ ->
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
                    mViewModel.setMaxMoney()
                }

                override fun getOtherText(): String {
                    return ContextCompat.getString(requireContext(), R.string.keyboard_max)
                }
            })
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
                val value: Int = when (tab?.position) {
                    0 -> 100
                    1 -> 25
                    2 -> 50
                    3 -> 75
                    else -> 100
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

    override fun initData() {
        super.initData()
        val betAmount = arguments?.getString(BET_AMOUNT_MONEY) ?: "0"
        val decimalDigitsCount = getDecimalDigitsCount(betAmount)
        mViewModel.setDecimalNumber(decimalDigitsCount)
        mViewModel.setAmountMoney(betAmount.toMoneyForScale(decimalDigitsCount))
    }

    override fun initListener() {
        mBinding.apply {
            btn100.setOnClickListener { mViewModel.setNumber(10000) }
            btn500.setOnClickListener { mViewModel.setNumber(50000) }
            btn2000.setOnClickListener { mViewModel.setNumber(200000) }
            btn5000.setOnClickListener { mViewModel.setNumber(500000) }
            btnBack.setOnClickListener { mViewModel.backNumber() }
            btnClear.setOnClickListener { mViewModel.clearNumber() }
            btnDouble.setOnClickListener { mViewModel.doubleNumber() }
            btnCollapse.setOnClickListener {
                mBinding.groupKeyboard.isVisible = false
            }
            btnPartSettle.clickNoRepeat {
                onEarlySettleClick?.invoke(
                    mViewModel.editValue
                )
                dismiss()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    fun setOnEarlySettleListener(listener: (money: String) -> Unit) {
        this.onEarlySettleClick = listener
    }

    override fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
            val money = it.ifEmpty {
                "0"
            }
            mBinding.tvBetMoney.text = getString(
                R.string.refund_amount
            ).format(money)
            mBinding.earlySettleTvTip.isVisible = money.toMoney() != 0L
        }
    }

    private fun getDecimalDigitsCount(str: String): Int {
        return if (str.contains('.')) {
            str.substringAfter('.').length
        } else {
            mViewModel.decimalNumber
        }
    }
}