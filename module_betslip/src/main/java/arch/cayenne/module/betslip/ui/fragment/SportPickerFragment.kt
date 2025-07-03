package arch.cayenne.module.betslip.ui.fragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isInvisible
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
        fun newInstance(anchorY: Int, sportIds: List<Int>): SportPickerFragment {
            return SportPickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ANCHOR_Y, anchorY)
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
        ObjectAnimator.ofFloat(clContent, "translationY", -height.toFloat(), 0f).apply {
            duration = 300
            addListener(onStart = {
                mBinding.clFilter.visibility = View.VISIBLE
            })
            start()
        }
    }

    private fun collapseView() {
        val clContent = mBinding.clContent
        val targetHeight = clContent.height
        ObjectAnimator.ofFloat(clContent, "translationY", 0f, -targetHeight.toFloat()).apply {
            duration = 300
            addListener(onStart={
                mBinding.maskView.isInvisible = true
            },onEnd = {
                mBinding.clFilter.visibility = View.INVISIBLE
                dismiss()
            })
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