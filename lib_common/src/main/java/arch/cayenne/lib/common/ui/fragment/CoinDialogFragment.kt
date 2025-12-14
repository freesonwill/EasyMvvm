package arch.cayenne.lib.common.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import arch.cayenne.lib.base.ui.fragment.BasePositionDialogFragment
import arch.cayenne.lib.common.R
import arch.cayenne.lib.common.data.constants.BaseCurrencyData
import arch.cayenne.lib.common.databinding.FragmentCoinDialogBinding
import arch.cayenne.lib.common.ui.adapter.CurrencyAdapter
import arch.cayenne.lib.common.ui.viewmodel.CoinDialogViewModel
import com.blankj.utilcode.util.ScreenUtils
import kotlin.reflect.KClass

/**
 * 币种选择下拉弹窗
 */
class CoinDialogFragment :
    BasePositionDialogFragment<CoinDialogViewModel, FragmentCoinDialogBinding>() {

    override val vbClass: KClass<FragmentCoinDialogBinding> = FragmentCoinDialogBinding::class
    override val vmClass: KClass<CoinDialogViewModel> = CoinDialogViewModel::class
    private val coinAdapter: CurrencyAdapter by lazy { CurrencyAdapter() }
    private var listener: DialogDismissListener? = null

    private val mockList: List<BaseCurrencyData> = listOf(
        BaseCurrencyData.CurrencyContentData2(1,"", "USDT", "", "U"),
        BaseCurrencyData.CurrencyContentData2(2,"", "BTC", "", "B"),
        BaseCurrencyData.CurrencyContentData2(3,"", "ETH", "", "E")
    )

    companion object {
        private const val LOCATION_OFFSET = "location"
        fun newInstance(offset: Int): CoinDialogFragment {
            val b = Bundle()
            b.putInt(LOCATION_OFFSET, offset)
            return CoinDialogFragment().apply {
                arguments = b
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        with(mBinding) {
            rvCurrency.adapter = coinAdapter
            rvCurrency.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            coinAdapter.submitList(mockList)
        }
        listener?.onShow()
    }

    override fun initListener() {

    }

    override fun setDialogPosition(w: Window) {
        val offset = requireArguments().getInt(LOCATION_OFFSET, -1)
        if (offset == -1)
            return
        val layoutParams = w.attributes
        layoutParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        layoutParams.y = offset
        layoutParams.width = ScreenUtils.getScreenWidth()
        w.attributes = layoutParams
        mBinding.root.visibility = View.VISIBLE
        removeDim()
    }

    override fun onDestroyView() {
        listener?.onDismiss()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener = null
    }

    fun setDismissListener(listener: DialogDismissListener) {
        this.listener = listener
    }

    interface DialogDismissListener {
        fun onDismiss()
        fun onShow()
    }
}