package arch.cayenne.module.betslip.ui.fragment

import android.animation.ObjectAnimator
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.DimController
import arch.cayenne.lib.common.data.constants.AnimationConstants
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlin.reflect.KClass

class SportPickerFragment private constructor() :
    BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>() {

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

    private val dimController by lazy {
        DimController.instance
    }

    override fun initView(savedInstanceState: Bundle?) {
        initDim()

        mBinding.root.visibility = View.INVISIBLE
        mBinding.rvSport.adapter = sportAdapter
        (mBinding.rvSport.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
    }

    override fun initData() {
        requireArguments().getIntArray(Config.VALUE_SELECTED_SPORT_ID)?.let { sportIds ->
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
    }

    override suspend fun createObserver() {
        mViewModel.onSportListener.observe(viewLifecycleOwner) {
            sportAdapter.submitList(it) {
                if (mBinding.root.visibility != View.VISIBLE) {
                    mBinding.root.post {
                        expandView()
                    }
                }
            }
        }
    }

    private fun getTopY(): Int {
        // 取得目標 View 在螢幕上的位置
        val location = IntArray(2)
        val targetView = mBinding.root
        targetView.getLocationOnScreen(location)
        return location[1]
    }

    private fun initDim() {

        // 獲取螢幕總高度
        val screenHeight = resources.displayMetrics.heightPixels

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, // 寬度填滿
            screenHeight + ViewUtils.getNavigationBarHeight(requireContext()),
            0,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT,
        )

        params.gravity = Gravity.TOP or Gravity.START
        dimController.hideDim()
        dimController.updateLayoutParams(params)
        mBinding.maskBottomView.setOnClickListener {
            collapseView()
        }
        dimController.setTranslationY(getTopY().toFloat())
    }

    private fun removeDim() {
        dimController.hideDim()
        mBinding.maskView.alpha = 0f
        mBinding.maskView.visibility = View.GONE
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
        mBinding.root.bringToFront()
        val root = mBinding.root
        val height = mBinding.clFilter.height

        val startY = getTopY().toFloat()
        ObjectAnimator.ofFloat(root, "translationY", -height.toFloat(), 0f).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            addUpdateListener {
                val value = it.animatedValue as Float
                dimController.showDim()
                dimController.setTranslationY(startY + (height + value))
            }
            doOnStart{
                mBinding.root.visibility = View.VISIBLE
            }
            start()
        }
    }

    fun collapseView() {
        mBinding.root.bringToFront()
        val root = mBinding.root
        val targetHeight = mBinding.clFilter.height
        ObjectAnimator.ofFloat(root, "translationY", 0f, -targetHeight.toFloat()).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            addUpdateListener {
                removeDim()
            }
            doOnEnd {
                mBinding.root.visibility = View.INVISIBLE
                dismiss()
            }
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
        resultBundle.putIntArray(
            Config.VALUE_SELECTED_SPORT_ID,
            bean.map { it.sportId }.toIntArray()
        )
        collapseView()
    }

    override fun onBackPressed(): Boolean {
        collapseView()
        return super.onBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dimController.reset()
    }
}