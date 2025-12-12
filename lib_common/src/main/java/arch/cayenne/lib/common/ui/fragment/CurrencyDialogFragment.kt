package arch.cayenne.lib.common.ui.fragment

import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Outline
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.data.constants.StatusBarMode
import arch.cayenne.lib.base.data.model.StatusBarConfig
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.FragmentCurrencyDialogBinding
import arch.cayenne.lib.common.ui.adapter.CurrencyAdapter
import arch.cayenne.lib.common.ui.adapter.CurrencySettingAdapter
import arch.cayenne.lib.common.ui.adapter.GridSpacingItemDecoration
import arch.cayenne.lib.common.ui.viewmodel.BalanceViewModel
import arch.cayenne.lib.common.ui.viewmodel.CurrencyDialogViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import kotlin.reflect.KClass
import org.koin.androidx.viewmodel.ext.android.viewModel

class CurrencyDialogFragment constructor() : BasePositionDialogFragment<CurrencyDialogViewModel, FragmentCurrencyDialogBinding>() {
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
        if (offset == -1) return

        val layoutParams = w.attributes
        if(isPortrait) {
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
                    // 取得目標 View (假設是 mBinding.clContent，請依您的 xml id 為準)
                    val targetView = mBinding.clCurrencyRoot

                    // 1. 設定動畫軸心為 View 的中心點
                    targetView.pivotX = targetView.width / 2f
                    targetView.pivotY = targetView.height / 2f

                    // 2. 設定初始狀態：縮小且透明
                    targetView.scaleX = 0f
                    targetView.scaleY = 0f
                    targetView.alpha = 0f
                    targetView.visibility = View.VISIBLE

                    // 4. 開始展開動畫
                    targetView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setDuration(200)
                        .setInterpolator(DecelerateInterpolator())
                        .start()
                }

            }
        })


        mBinding.root.visibility = View.VISIBLE
        removeDim()
    }

    override val vbClass: KClass<FragmentCurrencyDialogBinding> = FragmentCurrencyDialogBinding::class
    override val vmClass: KClass<CurrencyDialogViewModel> = CurrencyDialogViewModel::class

    val currencyAdapter: CurrencyAdapter by lazy { CurrencyAdapter() }
    val currencySettingAdapter: CurrencySettingAdapter by lazy { CurrencySettingAdapter() }


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            clCurrencyRoot.setOnClickListener {
               doExitAnim()
            }

            rvCurrency.adapter = currencyAdapter
            rvCurrency.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            rvCurrencySetting.adapter = currencySettingAdapter
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

    }

    override fun initData() {
        super.initData()
        balanceViewModel.getUserCurrency()
    }

    override fun initListener() {
        balanceViewModel.onUserCurrencyChange.observe(viewLifecycleOwner) { (fiat, crypto) ->
            val list = arrayListOf<BaseCurrencyData>()
            if (fiat.isNotEmpty()) {
                list.add(BaseCurrencyData.CurrencyTitleData(getString(R.string.fiat)))
                list.addAll(fiat)
            }
            if (crypto.isNotEmpty()) {
                list.add(BaseCurrencyData.CurrencyTitleData(getString(R.string.crypto)))
                list.addAll(crypto)
            }
            currencyAdapter.submitList(list)
            currencySettingAdapter.submitList(fiat+crypto)
        }
    }

    override fun onStart() {
        super.onStart()
        StatusBarConfig.statusBarType = if(requireArguments().getBoolean(IS_PORTRAIT)) StatusBarMode.DRAW_BEHIND() else StatusBarMode.FULLSCREEN
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
}