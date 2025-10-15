package arch.cayenne.module.order.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseBottomSheetFragment
import arch.cayenne.module.betslip.R
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentOrderSportFilterDialogBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class OrderSportFilterDialogFragment private constructor() :
    BaseBottomSheetFragment<SportPickerViewModel, FragmentOrderSportFilterDialogBinding>() {

    companion object {
        const val KEY_SELECTED_SPORT_ID = "key_selected_sport_id"
        fun newInstance(sportIds: List<Int> = emptyList()): OrderSportFilterDialogFragment {
            return OrderSportFilterDialogFragment().apply {
                if (sportIds.isNotEmpty()) {
                    val bundle = Bundle()
                    bundle.putIntArray(KEY_SELECTED_SPORT_ID, sportIds.toIntArray())
                    arguments = bundle
                }
            }
        }
    }

    override val vbClass: KClass<FragmentOrderSportFilterDialogBinding> =
        FragmentOrderSportFilterDialogBinding::class
    override val vmClass: KClass<SportPickerViewModel> = SportPickerViewModel::class

    private val sportAdapter: SportPickerAdapter by lazy {
        SportPickerAdapter(object : SportPickerAdapter.SportPickerListener {
            override fun onSportSelected(id: Int) {
                mViewModel.setSelectedById(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        isHorizontalGestureEnable = false
        mBinding.rvSport.adapter = sportAdapter
        (mBinding.rvSport.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun initData() {
        super.initData()
        val selectedIds = arguments?.getIntArray(KEY_SELECTED_SPORT_ID)?.toList()
        if (!selectedIds.isNullOrEmpty()) {
            mViewModel.setSelectedById(selectedIds.toIntArray())
        }
    }

    override fun initListener() {
        mBinding.btnReset.setOnClickListener {
            mViewModel.reset()
            sendResult()
        }
        mBinding.btnConfirm.setOnClickListener {
            sendResult()
        }
    }

    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.onSportListener.observe(viewLifecycleOwner) {
            val lastDataSize = sportAdapter.currentList.size
            sportAdapter.submitList(it) {
                if (lastDataSize != 0) {
                    mBinding.btnConfirm.text = getString(R.string.btn_sport_filter_confirm)
                }
            }
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