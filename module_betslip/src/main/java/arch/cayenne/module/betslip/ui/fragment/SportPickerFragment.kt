package arch.cayenne.module.betslip.ui.fragment

import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class SportPickerFragment: BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>() {

    companion object {
        const val TAG = "SportPickerFragment"
    }

    override val vbClass: KClass<FragmentSportPickerBinding> = FragmentSportPickerBinding::class
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

        expandView()
    }

    override fun initData() {
        val sportId = arguments?.getInt(Config.VALUE_SELECTED_SPORT_ID, -1) ?: -1
        mViewModel.setSelectedById(sportId)
    }

    override fun initListener() {
        mBinding.tvReset.setOnClickListener {
            mViewModel.setSelectedById(-1)
        }
        mBinding.tvConfirm.setOnClickListener {
            val bean = mViewModel.getSelectedSportBean()
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle().apply {
                putInt(Config.VALUE_SELECTED_SPORT_ID, bean.sportId)
                putString(Config.VALUE_SELECTED_SPORT_NAME, bean.sportName)
            })
            collapseView()
        }
    }

    override fun createObserver() {
        mViewModel.onSportListener.observe(viewLifecycleOwner){
            sportAdapter.submitList(it)
        }
    }

    fun dismiss() {
        mBinding.clFilter.isVisible = false
        mBinding.clFilter.post {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }

    private fun expandView() {
        val clFilter = mBinding.clFilter
        val height = clFilter.height
        clFilter.translationY = -height.toFloat()
        clFilter.alpha = 0f

        clFilter.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun collapseView() {
        val clFilter = mBinding.clFilter
        clFilter.animate()
            .translationY(-clFilter.height.toFloat())
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                dismiss() // 或其他關閉處理
            }
            .start()
    }
}