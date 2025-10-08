package arch.cayenne.lib.common.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.FragmentCurrencyDialogBinding
import arch.cayenne.lib.common.ui.adapter.CurrencyAdapter
import arch.cayenne.lib.common.ui.adapter.CurrencySettingAdapter
import arch.cayenne.lib.common.ui.viewmodel.CurrencyDialogViewModel
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import kotlin.reflect.KClass

class CurrencyDialogFragment private constructor() : BasePositionDialogFragment<CurrencyDialogViewModel, FragmentCurrencyDialogBinding>() {
    companion object {
        private const val LOCATION_Y = "locationY"

        fun newInstance(positionY: Int): CurrencyDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_Y, positionY)
            return CurrencyDialogFragment().apply {
                arguments = b
            }
        }
    }

    val mockList: List<BaseCurrencyData> = listOf(
        BaseCurrencyData.CurrencyTitleData("现金"),
        BaseCurrencyData.CurrencyContentData(R.drawable.ic_usa, "美元", "$100.00"),
        BaseCurrencyData.CurrencyContentData(R.drawable.ic_china, "人民币", "¥39900.00"),
        BaseCurrencyData.CurrencyTitleData("加密货币"),
        BaseCurrencyData.CurrencyContentData(R.drawable.ic_usdt, "USDT", "1100.11100"),
        BaseCurrencyData.CurrencyContentData(R.drawable.ic_btc, "BTC", "123.15"),
        BaseCurrencyData.CurrencyContentData(R.drawable.ic_eth, "ETH", "399.11"),
    )

    override fun setDialogPosition(w: Window) {
        val positionY = requireArguments().getInt(LOCATION_Y, -1)
        if (positionY == -1) return

        val layoutParams = w.attributes
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = positionY
        w.attributes = layoutParams
        mBinding.root.visibility = View.VISIBLE
        removeDim()
    }

    override val vbClass: KClass<FragmentCurrencyDialogBinding> = FragmentCurrencyDialogBinding::class
    override val vmClass: KClass<CurrencyDialogViewModel> = CurrencyDialogViewModel::class

    val currencyAdapter: CurrencyAdapter by lazy { CurrencyAdapter() }
    val currencySettingAdapter: CurrencySettingAdapter by lazy { CurrencySettingAdapter() }


    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrency.adapter = currencyAdapter
            rvCurrency.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            currencyAdapter.submitList(mockList)

            rvCurrencySetting.adapter = currencySettingAdapter
            rvCurrencySetting.layoutManager = GridLayoutManager(requireContext(), 2)
            val itemDecoration = GridSpacingItemDecoration(
                spanCount = 2,
                horizontalSpacing = 6.dp2px,
                verticalSpacing = 8.dp2px,
                includeEdge = false // 確保邊緣沒有空隙
            )
            rvCurrencySetting.addItemDecoration(itemDecoration)
            currencySettingAdapter.submitList(mockList.filterIsInstance<BaseCurrencyData.CurrencyContentData>())

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
        }
    }

    override fun initListener() {

    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val horizontalSpacing: Int,
        private val verticalSpacing: Int,
        private val includeEdge: Boolean = false
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            if (position == RecyclerView.NO_POSITION) {
                return
            }

            val layoutManager = parent.layoutManager as? GridLayoutManager
                ?: throw IllegalStateException("This ItemDecoration can only be used with a GridLayoutManager.")

            // 確保列數匹配
            if (layoutManager.spanCount != spanCount) {
                throw IllegalStateException("The spanCount of the GridLayoutManager must match the one set in the ItemDecoration.")
            }

            val column = position % spanCount // item column

            if (includeEdge) {
                // 這種算法會在最左和最右邊都留出空間
                outRect.left = horizontalSpacing - column * horizontalSpacing / spanCount
                outRect.right = (column + 1) * horizontalSpacing / spanCount
                if (position < spanCount) { // top edge
                    outRect.top = verticalSpacing
                }
                outRect.bottom = verticalSpacing // item bottom
            } else {
                // 這種算法確保最左和最右邊沒有空隙
                outRect.left = column * horizontalSpacing / spanCount
                outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount

                if (position >= spanCount) {
                    outRect.top = verticalSpacing // non-top edge
                }
            }
        }
    }
}