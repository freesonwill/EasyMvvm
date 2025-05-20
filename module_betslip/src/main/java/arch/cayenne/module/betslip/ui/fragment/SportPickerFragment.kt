package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class SportPickerFragment: BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>() {

    override val vbClass: KClass<FragmentSportPickerBinding> = FragmentSportPickerBinding::class
    override val vmClass: KClass<SportPickerViewModel> = SportPickerViewModel::class

    private var anchorY = 0
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

    override fun initData() {
        val sportId = arguments?.getInt(Config.VALUE_SELECTED_SPORT, -1) ?: -1
        mViewModel.setSelectedById(sportId)
    }

    override fun initListener() {
        mBinding.tvReset.setOnClickListener {
            mViewModel.setSelectedById(-1)
        }
        mBinding.tvConfirm.setOnClickListener {
            val id = mViewModel.getSelectedSportId()
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
                putInt(Config.VALUE_SELECTED_SPORT, id)
            })
        }
    }

    override fun createObserver() {
        mViewModel.onSportListener.observe(viewLifecycleOwner){
            sportAdapter.submitList(it)
        }
    }

    fun setAnchorY(y: Int) {
        anchorY = y
    }
}