package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.FragmentCurrencyDialogBinding
import arch.cayenne.lib.common.ui.adapter.CurrencyAdapter
import arch.cayenne.lib.common.ui.viewmodel.CurrencyDialogViewModel
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

    val adapter: CurrencyAdapter by lazy { CurrencyAdapter() }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrency.adapter = adapter
            rvCurrency.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter.submitList(mockList)
        }
    }

    override fun initListener() {

    }
}