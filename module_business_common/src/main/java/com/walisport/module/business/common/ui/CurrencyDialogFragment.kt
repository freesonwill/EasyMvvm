package com.walisport.module.business.common.ui

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Outline
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.ui.viewmodel.EmptyViewModel
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.FragmentCurrencyDialogBinding
import arch.cayenne.lib.common.ui.adapter.CurrencyAdapter
import arch.cayenne.lib.common.ui.adapter.CurrencySettingAdapter
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.helper.showToast
import com.blankj.utilcode.util.SizeUtils
import com.walisport.module.business.common.viewmodel.BalanceViewModel
import kotlin.reflect.KClass
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrencyDialogFragment constructor() :
    BasePositionDialogFragment<EmptyViewModel, FragmentCurrencyDialogBinding>() {

    companion object {
        private const val LOCATION_OFFSET = "locationOffset"
        private const val IS_PORTRAIT = "isPortrait"

        fun newInstance(isPortrait: Boolean, offset: Int): CurrencyDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_OFFSET, offset)
            b.putBoolean(IS_PORTRAIT, isPortrait)
            return CurrencyDialogFragment().apply {
                arguments = b
            }
        }
    }

    private var dismissListener: (() -> Unit)? = null
    private var onClickListener: ((BaseCurrencyData.CurrencyContentData) -> Unit)? = null
    private val balanceViewModel: BalanceViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun cancel() {
                if (!mBinding.root.isEnabled) return
                // 讓系統其他地方調用 dismiss 時也會觸發動畫
                if (mBinding.root.translationX == 0f) {
                    doExitAnim()
                } else {
                    super.dismiss()
                }
            }
        }.apply {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION)
        }
    }

    override fun setDialogPosition(w: Window) {
        val isPortrait = requireArguments().getBoolean(IS_PORTRAIT)
        val offset = requireArguments().getInt(LOCATION_OFFSET, -1)
        if (offset == -1)
            return
        val layoutParams = w.attributes
        if (isPortrait) {
            layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            layoutParams.y = offset
        } else {
            layoutParams.gravity = Gravity.START or Gravity.CENTER_HORIZONTAL
            layoutParams.x = offset
        }
        w.attributes = layoutParams
        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                mBinding.root.post {
                    val targetView = mBinding.clCurrencyRoot
                    // 1. 設定動畫軸心為 View 的中心點
                    targetView.pivotX = targetView.width / 2f
                    targetView.pivotY = targetView.height / 2f
                    // 2. 設定初始狀態：縮小且透明
                    targetView.scaleX = 0f
                    targetView.scaleY = 0f
                    targetView.alpha = 0f

                    // 4. 開始展開動畫
                    targetView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(DecelerateInterpolator())
                        .withStartAction {
                            targetView.visibility = View.VISIBLE
                        }
                        .start()
                }
            }
        })
        removeDim()
    }

    override val vbClass: KClass<FragmentCurrencyDialogBinding> =
        FragmentCurrencyDialogBinding::class
    override val vmClass: KClass<EmptyViewModel> = EmptyViewModel::class

    private val currencyAdapter: CurrencyAdapter by lazy {
        CurrencyAdapter {
            this.onClickListener?.invoke(it)
            doExitAnim()
        }
    }
    private val settingAdapter: CurrencySettingAdapter by lazy { CurrencySettingAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            clCurrencyRoot.visibility = View.INVISIBLE
            clCurrencyRoot.setOnClickListener {
                doExitAnim()
            }
            rvCurrency.adapter = currencyAdapter
            rvCurrency.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvCurrencySetting.adapter = settingAdapter
            rvCurrencySetting.layoutManager = GridLayoutManager(requireContext(), 2)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 2,
                horizontalSpacing = 6.dp2px,
                verticalSpacing = 8.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvCurrencySetting.addItemDecoration(itemDecoration)
            ivSetting.clickNoRepeat {
                ObjectAnimator.ofFloat(
                    clContent, "translationX", 0f, -clContent.measuredWidth.toFloat()
                ).apply {
                    duration = 200
                    interpolator = DecelerateInterpolator()
                    start()
                }
                ObjectAnimator.ofFloat(
                    clContent2, "translationX", 0f, -clContent2.measuredWidth.toFloat()
                ).apply {
                    duration = 200
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
            ivBack.clickNoRepeat {
                ObjectAnimator.ofFloat(
                    clContent, "translationX", -clContent.measuredWidth.toFloat(), 0f
                ).apply {
                    duration = 200
                    interpolator = DecelerateInterpolator()
                    start()
                }
                ObjectAnimator.ofFloat(
                    clContent2, "translationX", -clContent2.measuredWidth.toFloat(), 0f
                ).apply {
                    duration = 200
                    interpolator = DecelerateInterpolator()
                    start()
                }
            }
            ceSearch.addTextChangedListener(
                afterTextChanged = { editable ->
                    val keyword = editable.toString()
                    balanceViewModel.search(keyword)
                }
            )
        }
        //解決搜尋時動態改變Recycleview高度後，blurView下方左右的圓角消失問題
        mBinding.blurView.apply {
            clipToOutline = true // 開啟裁剪
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(
                        0,
                        0,
                        view.width,
                        view.height,
                        SizeUtils.dp2px(9f).toFloat()
                    )
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        balanceViewModel.getUserCurrency()
    }

    override fun initListener() {
        balanceViewModel.onUserCurrencyChange.observe(viewLifecycleOwner) { (fiat, crypto) ->
            val list = arrayListOf<BaseCurrencyData>()
            if (fiat.isEmpty() && crypto.isEmpty()) {
                list.add(BaseCurrencyData.CurrencyTitleData(getString(R.string.currency_empty)))
            }
            if (fiat.isNotEmpty()) {
                list.add(BaseCurrencyData.CurrencyTitleData(getString(R.string.fiat)))
                list.addAll(fiat)
            }
            if (crypto.isNotEmpty()) {
                list.add(BaseCurrencyData.CurrencyTitleData(getString(R.string.crypto)))
                list.addAll(crypto)
            }
            currencyAdapter.submitList(list)
            mBinding.rvCurrency.doOnPreDraw {
                mBinding.blurView.invalidateOutline()
            }
            settingAdapter.submitList(fiat)
        }
        settingAdapter.setOnItemClickListener(object : CurrencySettingAdapter.OnItemClickListener {
            override fun onItemClick(bean: BaseCurrencyData.CurrencyContentData) {
                balanceViewModel.changeFiat(bean.ccy).observe(viewLifecycleOwner) { success ->
                    if (success) {
                        balanceViewModel.getUserCurrency()
                    } else {
                        showToast(R.string.error_net.getString())
                    }
                }
                settingAdapter.updateSelect(bean.ccy)
            }
        })
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType =
            if (requireArguments().getBoolean(IS_PORTRAIT)) StatusBarMode.DRAW_BEHIND() else StatusBarMode.FULLSCREEN
        setStatusBar(StatusBarConfig, mBinding.root)
    }

    private fun doExitAnim() {
        if (!mBinding.root.isEnabled) return
        mBinding.root.isEnabled = false
        val targetView = mBinding.clCurrencyRoot
        targetView.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                super.dismiss()
            }
            .withStartAction {
                dismissListener?.invoke()
            }
            .start()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        this.dismissListener = listener
    }

    fun setOnItemClickListener(listener: (BaseCurrencyData.CurrencyContentData) -> Unit) {
        this.onClickListener = listener
    }
}