package arch.cayenne.module.betslip.ui.fragment

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class SportPickerFragment private constructor(): BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>() {

    companion object {
        const val TAG = "SportPickerFragment"
        private const val ANCHOR_Y = "anchorY"
        fun newInstance(anchorY: Int, sportId: Int): SportPickerFragment {
            return SportPickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ANCHOR_Y, anchorY)
                    putInt(Config.VALUE_SELECTED_SPORT, sportId)
                }
            }
        }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            mBinding.clFilter.visibility = View.INVISIBLE
            val anchorY = requireArguments().getInt(ANCHOR_Y, 0)
            val lp = mBinding.clFilter.layoutParams as ConstraintLayout.LayoutParams
            lp.topMargin = anchorY
            mBinding.clFilter.layoutParams = lp
        }
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
            collapseView()
        }
        mBinding.maskView.setOnClickListener {
            collapseView()
        }
        mBinding.topMaskView.setOnClickListener {
            collapseView()
        }
    }

    override fun createObserver() {
        mViewModel.onSportListener.observe(viewLifecycleOwner){
            sportAdapter.submitList(it) {
                if (mBinding.clFilter.visibility != View.VISIBLE) {
                    mBinding.clFilter.post {
                        expandView()
                    }
                }
            }
        }
    }

    fun dismiss() {
        mBinding.clFilter.visibility = View.INVISIBLE
        parentFragmentManager.setFragmentResult(Config.KEY_RESULT, Bundle())
        mBinding.clFilter.post {
            parentFragmentManager.beginTransaction()
                .setReorderingAllowed(true) // 避免 layout 重新整理過猛
                .remove(this)
                .commitAllowingStateLoss()
        }
    }

    private fun expandView() {
        val clFilter = mBinding.clFilter

        val targetHeight = clFilter.height

        // 用 ValueAnimator 動畫拉高
        val animator = ValueAnimator.ofInt(1, targetHeight)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val lp = clFilter.layoutParams
            lp.height = value
            clFilter.layoutParams = lp
        }
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.doOnStart {
            val layoutParams = clFilter.layoutParams
            layoutParams.height = 1
            clFilter.layoutParams = layoutParams
            clFilter.visibility = View.VISIBLE
        }
        animator.start()
    }

    private fun collapseView() {
        val clFilter = mBinding.clFilter

        val targetHeight = clFilter.height

        // 用 ValueAnimator 動畫拉高
        val animator = ValueAnimator.ofInt(targetHeight, 0)
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val lp = clFilter.layoutParams
            lp.height = value
            clFilter.layoutParams = lp
        }
        animator.duration = 300
        animator.interpolator = DecelerateInterpolator()
        animator.doOnEnd {
            dismiss()
        }
        animator.start()
    }

    fun show(manager: FragmentManager, containerId: Int) {
        val lastFragment = manager.findFragmentByTag(TAG)
        if (lastFragment == null || !lastFragment.isAdded) {
            manager.beginTransaction()
                .setReorderingAllowed(true)
                .add(containerId, this, this.javaClass.simpleName)
                .commit()
        }
    }
}