package arch.cayenne.module.betslip.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.core.animation.addListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.data.constants.AnimationConstants
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class SportPickerFragment private constructor(): BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>() {

    companion object {
        fun newInstance(sportIds: List<Int>): SportPickerFragment {
            return SportPickerFragment().apply {
                arguments = Bundle().apply {
                    putIntArray(Config.VALUE_SELECTED_SPORT_ID, sportIds.toIntArray())
                }
            }
        }
    }

    override val vbClass: KClass<FragmentSportPickerBinding> = FragmentSportPickerBinding::class
    override val vmClass: KClass<SportPickerViewModel> = SportPickerViewModel::class
    private val resultBundle by lazy {
        Bundle()
    }

    private val sportAdapter: SportPickerAdapter by lazy {
        SportPickerAdapter(object : SportPickerAdapter.SportPickerListener {
            override fun onSportSelected(id: Int) {
                mViewModel.setSelectedById(id)
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.clFilter.visibility = View.INVISIBLE
        mBinding.maskView.visibility = View.INVISIBLE
        mBinding.rvSport.adapter = sportAdapter
        (mBinding.rvSport.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun initData() {
        requireArguments().getIntArray(Config.VALUE_SELECTED_SPORT_ID)?.let {  sportIds ->
            mViewModel.setSelectedById(sportIds)
        }
    }

    override fun initListener() {
        mBinding.tvReset.setOnClickListener {
            mViewModel.reset()
            sendResult()
        }
        mBinding.tvConfirm.setOnClickListener {
            sendResult()
        }
        mBinding.maskView.setOnClickListener {
            collapseView()
        }
    }

    override suspend fun createObserver() {
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
        if (parentFragment != null) {
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
            mBinding.clFilter.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true) // 避免 layout 重新整理過猛
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun expandView() {
        val clContent = mBinding.clContent
        val height = clContent.height
        val contentAnimate =
            ObjectAnimator.ofFloat(clContent, "translationY", -height.toFloat(), 0f).apply {
                addListener(onStart = {
                    mBinding.clFilter.visibility = View.VISIBLE
                    mBinding.maskView.visibility = View.VISIBLE
                })
            }
        val maskAlphaAnimate = ObjectAnimator.ofFloat(mBinding.maskView, "alpha", 0f, 0.75f)
        AnimatorSet().apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            playTogether(contentAnimate, maskAlphaAnimate)
            start()
        }
    }

    fun collapseView() {
        mBinding.root.bringToFront()
        val clContent = mBinding.clContent
        val targetHeight = clContent.height
        val contentAnimate =
            ObjectAnimator.ofFloat(clContent, "translationY", 0f, -targetHeight.toFloat()).apply {
                addListener(onEnd = {
                    mBinding.clFilter.visibility = View.INVISIBLE
                    mBinding.maskView.visibility = View.INVISIBLE
                    dismiss()
                })
            }
        val maskAlphaAnimate = ObjectAnimator.ofFloat(mBinding.maskView, "alpha", 0.75f, 0f)
        AnimatorSet().apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            playTogether(contentAnimate, maskAlphaAnimate)
            start()
        }
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

    private fun sendResult() {
        val bean = mViewModel.getSelectedSportBean()
        resultBundle.putIntArray(Config.VALUE_SELECTED_SPORT_ID, bean.map { it.sportId }.toIntArray())
        collapseView()
    }
}