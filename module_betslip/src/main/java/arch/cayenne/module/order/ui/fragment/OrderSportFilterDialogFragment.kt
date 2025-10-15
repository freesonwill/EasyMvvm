package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentOrderSportFilterDialogBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class OrderSportFilterDialogFragment private constructor(): BaseBottomSheetFragment<SportPickerViewModel, FragmentOrderSportFilterDialogBinding>() {

    companion object {
        fun newInstance(): OrderSportFilterDialogFragment {
            return OrderSportFilterDialogFragment()
        }
    }

    override val vbClass: KClass<FragmentOrderSportFilterDialogBinding> = FragmentOrderSportFilterDialogBinding::class
    override val vmClass: KClass<SportPickerViewModel> = SportPickerViewModel::class

    private val sportAdapter: SportPickerAdapter by lazy {
        SportPickerAdapter(object : SportPickerAdapter.SportPickerListener {
            override fun onSportSelected(id: Int) {
                mViewModel.setSelectedById(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.rvSport.adapter = sportAdapter
        (mBinding.rvSport.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun initListener() {
        mBinding.btnReset.setOnClickListener {
            mViewModel.reset()
            sendResult()
        }
        mBinding.btnReset.setOnClickListener {
            sendResult()
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.onSportListener.observe(viewLifecycleOwner) {
            sportAdapter.submitList(it)
        }
    }

    private fun sendResult() {
        val bean = mViewModel.getSelectedSportBean()
        parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
            putIntArray(
                Config.VALUE_SELECTED_SPORT_ID,
                bean.map { it.sportId }.toIntArray()
            )
        })
        dismiss()
    }
}