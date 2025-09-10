package arch.cayenne.module.betslip.ui.dialog

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoneyForScale
import arch.cayenne.lib.common.utils.ext.TabLayoutExt
import arch.cayenne.lib.common.utils.ext.TabLayoutExt.addOnTabSelectedListener2
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import arch.cayenne.lib.skin.widget.SkinnableTabLayout
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.databinding.FragmentEarlySettledNumberKeyboardBinding
import arch.cayenne.module.betslip.ui.viewmodel.EarlySettledKeyboardViewModel
import com.google.android.material.tabs.TabLayout
import kotlin.reflect.KClass


/**
 * 提前结算报价
 * */
class BetSlipEarlySettledFragment :
    BasePreLoadBottomSheetFragment<EarlySettledKeyboardViewModel, FragmentEarlySettledNumberKeyboardBinding>() {

    companion object {
        private const val BET_AMOUNT_MONEY = "bet_amount_money"
        private const val BET_AMOUNT_MIN = "bet_amount_min"
        private const val BET_AMOUNT_CURRENCY = "bet_amount_currency"
        private const val TAG = "BetSlipEarlySettledFragment"

        fun create(fragment: Fragment): BetSlipEarlySettledFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? BetSlipEarlySettledFragment
            return if (f == null) {
                val newF = BetSlipEarlySettledFragment()
                newF.customAttach(fragment, TAG)
                newF
            } else {
                f
            }
        }

        fun show(
            fragment: Fragment,
            money: String,
            minAmount: String,
            currency: String
        ): BetSlipEarlySettledFragment {
            val f = fragment.childFragmentManager.findFragmentByTag(TAG) as? BetSlipEarlySettledFragment
            val newF = f ?: create(fragment)
            newF.apply {
                arguments = Bundle().apply {
                    putString(BET_AMOUNT_MONEY, money)
                    putString(BET_AMOUNT_MIN, minAmount)
                    putString(BET_AMOUNT_CURRENCY, currency)
                }
            }
            newF.customShow()
            return newF
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
                showKeyboard()
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

    private fun setCurrency() {
        val currency = requireArguments().getString(BET_AMOUNT_CURRENCY) ?: ""
        mViewModel.setMoneyCurrency(currency)
    }

    private fun setAmount() {
        val betAmount = requireArguments().getString(BET_AMOUNT_MONEY) ?: "0"
        val minAmount = requireArguments().getString(BET_AMOUNT_MIN) ?: "0"
        val decimalDigitsCount = getDecimalDigitsCount(betAmount)
        mViewModel.setDecimalNumber(decimalDigitsCount)
        mViewModel.setAmountMoney(
            betAmount.toMoneyForScale(decimalDigitsCount),
            minAmount.toMoney()
        )
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
        tabLayout.addOnTabSelectedListener2(object : TabLayoutExt.OnTabSelectedListener2 {
            override fun onTabSelected(tab:TabLayout.Tab, isTabClick: Boolean) {
                val value: Int = when (tab.position) {
                    0 -> 100
                    1 -> 25
                    2 -> 50
                    3 -> 75
                    else -> 100
                }
                mViewModel.setPercentNumber(value)
            }

            override fun onTabUnselected(tab:TabLayout.Tab, isTabClick: Boolean) {
            }

            override fun onTabReselected(tab:TabLayout.Tab, isTabClick: Boolean) {
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
        mBinding.apply {
            btn100.setOnClickListener { mViewModel.setNumber(10000) }
            btn500.setOnClickListener { mViewModel.setNumber(50000) }
            btn1000.setOnClickListener { mViewModel.setNumber(100000) }
            btn2000.setOnClickListener { mViewModel.setNumber(200000) }
            btn5000.setOnClickListener { mViewModel.setNumber(500000) }
            btnBack.setOnClickListener { mViewModel.backNumber() }
            btnClear.setOnClickListener { mViewModel.clearNumber() }
            btnDouble.setOnClickListener { mViewModel.doubleNumber() }
            btnCollapse.setOnClickListener {
                hideKeyboard()
            }
            btnPartSettle.clickNoRepeat {
                sendMoney()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    private fun hideKeyboard() {
        ViewUtils.collapseView(mBinding.clKeyboard, mBinding.ivFakerView)
        mBinding.etMoney.clearFocus()
        mBinding.clMoney.isFocusableInTouchMode = false
        mBinding.clMoney.isFocusable = false
    }

    private fun showKeyboard() {
        if (!mBinding.clKeyboard.isVisible) {
            ViewUtils.expandView(mBinding.clKeyboard, mBinding.ivFakerView)
            mBinding.etMoney.requestFocus()
            mBinding.clMoney.isFocusableInTouchMode = true
            mBinding.clMoney.isFocusable = true
        }
    }


    fun setOnEarlySettleListener(listener: (money: String) -> Unit) {
        this.onEarlySettleClick = listener
    }

    override suspend fun createObserver() {
        mViewModel.onEditNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            val length = it.length
            mBinding.etMoney.setSelection(length)
            val money = it.ifEmpty {
                "0.00"
            }
            mBinding.tvBetMoney.text =
                getString(R.string.refund_amount).format(mViewModel.currencySymbol, money)
        }
        mViewModel.currencySymbolListener.observe(viewLifecycleOwner) {
            mBinding.tvMoney.text = it
        }
        mViewModel.onOverNumberListener.observe(viewLifecycleOwner) { overNumber ->
            overNumber.msg?.let {
                showToast(it)
            }
        }
    }

    private fun getDecimalDigitsCount(str: String): Int {
        val decimalDigits = str.substringAfter('.', missingDelimiterValue = "")
        val actualLength = decimalDigits.length
        return maxOf(actualLength, mViewModel.decimalNumber)
    }

    private fun sendMoney() {
        val minAmount = mViewModel.minMoney
        val curAmount = mViewModel.editValue.toMoney()
        if (curAmount < minAmount) {
            showToast(getString(R.string.hint_less_amount_early_settle))
        } else {
            onEarlySettleClick?.invoke(
                mViewModel.editValue
            )
            dismiss()
        }
    }

    override fun customShow() {
        setCurrency()
        setAmount()
        super.customShow()
    }
}