package com.walisport.module.live.ui

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import arch.cayenne.lib.base.ui.fragment.BaseDialogFragment
import arch.cayenne.lib.common.ui.view.NumberKeyboardView
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.skin.widget.SportTabLayout
import com.google.android.material.tabs.TabLayout
import com.walisport.module.live.R
import com.walisport.module.live.databinding.FragmentEarlySettledNumberKeyboardBinding
import com.walisport.module.live.ui.viewmodel.LiveEarlySettledKeyboardViewModel
import kotlin.reflect.KClass

/**
 * 提前结算报价
 * */
class LiveEarlySettledKeyboardFragment private constructor() :
    BaseDialogFragment<LiveEarlySettledKeyboardViewModel, FragmentEarlySettledNumberKeyboardBinding>() {
    private val betIdKey = "bet_id"
    private var betId: String = ""

    companion object {

        fun instance(price: String): LiveEarlySettledKeyboardFragment {
            return LiveEarlySettledKeyboardFragment().apply {
                arguments = Bundle().apply {
                    putString(betIdKey, price)
                }
            }
        }
    }

    override val vbClass: KClass<FragmentEarlySettledNumberKeyboardBinding>
        get() = FragmentEarlySettledNumberKeyboardBinding::class
    override val vmClass: KClass<LiveEarlySettledKeyboardViewModel>
        get() = LiveEarlySettledKeyboardViewModel::class
    private var onEarlySettleClick: ((money: String,expectPrice:String) -> Unit)? = null

    override fun initView(savedInstanceState: Bundle?) {
        dialog?.setCanceledOnTouchOutside(true)
        betId = arguments?.getString(betIdKey) ?: ""
        mViewModel.earlySettledPrice(betId)
        with(mBinding) {
            initTab(tabLayout = llTab)
            ViewUtils.hideKeyboard(requireContext(), etMoney)
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
                    return ContextCompat.getString(requireContext(), R.string.keyboard_max)
                }
            })
            btn100.clickNoRepeat { mViewModel.setNumber(100) }
            btn500.clickNoRepeat { mViewModel.setNumber(500) }
            btn2000.clickNoRepeat { mViewModel.setNumber(2000) }
            btn5000.clickNoRepeat { mViewModel.setNumber(5000) }
            btnBack.clickNoRepeat { mViewModel.backNumber() }
            btnClear.clickNoRepeat { mViewModel.clearNumber() }
            btnDouble.clickNoRepeat { mViewModel.doubleNumber() }
            btnCollapse.clickNoRepeat { }
            btnPartSettle.clickNoRepeat {
                onEarlySettleClick?.invoke(
                    mViewModel.editNumber.value ?: "",mViewModel.prices.value?.price.toString() ?:""
                )
                dismiss()
            }
            btnCancel.clickNoRepeat { dismiss() }
            tvBetMoney.text = getString(R.string.refund_amount, mViewModel.prices.value?.price)
        }
    }

    override fun onStart() {
        super.onStart()
        setDialogPosition()
    }

    override val dialogBackground: Drawable?
        get() = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.black_20))

    private fun setDialogPosition() {
        dialog?.window?.apply {
            mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val layoutParams = attributes
                    layoutParams.width = resources.displayMetrics.widthPixels
                    layoutParams.height = resources.displayMetrics.heightPixels
                    layoutParams.gravity = Gravity.BOTTOM
                    attributes = layoutParams
                }
            })
        }
    }

    private fun initTab(tabLayout: SportTabLayout) {
        val array = resources.getStringArray(R.array.keyboard_percent)
        array.forEach { text ->
            // 添加新Tab
            val newTab = tabLayout.newTab()
            newTab.text = text
            tabLayout.addTab(newTab)
        }
        reflexPadding(tabLayout)
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

    fun setOnEarlySettleListener(listener: (money: String,expectPrice:String) -> Unit) {
        this.onEarlySettleClick = listener
    }

    override fun createObserver() {
        mViewModel.editNumber.observe(viewLifecycleOwner) {
            mBinding.etMoney.setText(it)
            mBinding.etMoney.setSelection(it.length)
        }
        mViewModel.prices.observe(viewLifecycleOwner) {
            mBinding.tvBetMoney.text = getString(R.string.refund_amount, it.price)
        }
    }
}