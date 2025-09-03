package arch.cayenne.module.betslip.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import arch.cayenne.lib.base.ui._interface.SimilarDialogInterface
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.base.ui.fragment.dim.DimController
import arch.cayenne.lib.base.ui.fragment.dim.DimInterface
import arch.cayenne.lib.common.data.constants.AnimationConstants
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.module.betslip.data.constants.Config
import arch.cayenne.module.betslip.databinding.FragmentSportPickerBinding
import arch.cayenne.module.betslip.ui.adapter.SportPickerAdapter
import arch.cayenne.module.betslip.ui.viewmodel.SportPickerViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class SportPickerFragment private constructor() :
    BaseFragment<SportPickerViewModel, FragmentSportPickerBinding>(), SimilarDialogInterface, DimInterface {

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

    private val dimController: DimController by lazy {
        DimController.getInstance(this)
    }
    private var dimView: View? = null

    private var isShow = false

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
        mBinding.maskView.setOnClickListener {
            collapseView()
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

    private fun initDim() {

        // 獲取螢幕總高度
        val screenHeight = resources.displayMetrics.heightPixels

        val navigatorHeight = ViewUtils.getNavigationBarHeight(requireContext())

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT, // 寬度填滿
            screenHeight + navigatorHeight,
            WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT,
        )

        params.gravity = Gravity.TOP or Gravity.START

        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val v = View(context).apply {
            setBackgroundColor(Color.BLACK)
            alpha = 0f
            dimView = this
            translationY = screenHeight.toFloat()
        }
        windowManager.addView(v, params)
    }

    fun dismiss() {
        if (parentFragment != null) {
            mBinding.clFilter.post {
                parentFragmentManager.beginTransaction()
                    .setReorderingAllowed(true) // 避免 layout 重新整理過猛
                    .remove(this)
                    .commitAllowingStateLoss()
            }
        }
    }

    private fun expandView() {
        if (dimController.findAnyShowing(this)) {
            parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
            dismiss()
            return
        }
        if (isShow) return
        isShow = true
        mBinding.root.bringToFront()
        val root = mBinding.clFilter
        val height = mBinding.clFilter.height

        ObjectAnimator.ofFloat(root, "translationY", -height.toFloat(), 0f).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            doOnStart{
                mBinding.root.visibility = View.VISIBLE
                dimView?.alpha = 0.75f
            }
            start()
        }
    }

    fun collapseView() {
        if (!isShow) return
        val dim = dimView ?: return
        isShow = false
        mBinding.root.bringToFront()
        val root = mBinding.clFilter
        val targetHeight = mBinding.clFilter.height.toFloat()
        val sheetAnimator = ObjectAnimator.ofFloat(root, "translationY", root.translationY, -targetHeight).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            doOnEnd {
                mBinding.root.visibility = View.INVISIBLE
                removeDim()
                dismiss()
            }
            doOnStart {
                parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
            }
        }
        val maskAnimator = ObjectAnimator.ofFloat(mBinding.maskView, "alpha", mBinding.maskView.alpha, 0f)
        val dimAnimator = ObjectAnimator.ofFloat(dim, "alpha", dimView?.alpha ?: 0.75f, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                dim.alpha = value
            }
        }

        maskAnimator.duration = sheetAnimator.duration
        maskAnimator.interpolator = sheetAnimator.interpolator
        dimAnimator.duration = sheetAnimator.duration
        dimAnimator.interpolator = sheetAnimator.interpolator

        AnimatorSet().apply {
            playTogether(sheetAnimator, dimAnimator, maskAnimator)
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

    private fun removeDim() {
        val dim = dimView ?: return
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.removeView(dim)
        dimView = null
    }

    override fun onBackPressed(): Boolean {
        collapseView()
        return super.onBackPressed()
    }

    override fun onDestroy() {
        if (isShow) {
            removeDim()
        }
        super.onDestroy()
    }

    override fun collapse(doSomething: (() -> Unit)?) {
        isShow = false
        mBinding.root.bringToFront()
        val root = mBinding.clFilter
        val targetHeight = mBinding.clFilter.height.toFloat()
        val sheetAnimator = ObjectAnimator.ofFloat(root, "translationY", root.translationY, -targetHeight).apply {
            duration = AnimationConstants.DIALOG_POPUP_DURATION
            doOnEnd {
                mBinding.root.visibility = View.INVISIBLE
                dismiss()
            }
            doOnStart {
                parentFragmentManager.setFragmentResult(Config.KEY_RESULT, resultBundle)
            }
        }
        val maskAnimator = ObjectAnimator.ofFloat(mBinding.maskView, "alpha", mBinding.maskView.alpha, 0f)
        val dimAnimator = ObjectAnimator.ofFloat(dimView, "alpha", dimView?.alpha ?: 0.75f, 0f).apply {
            addUpdateListener {
                val value = it.animatedValue as Float
                dimView?.alpha = value
            }
            doOnEnd {
                removeDim()
            }
            doOnStart {
                lifecycleScope.launch {
                    delay(AnimationConstants.DIALOG_POPUP_DURATION / 2)
                    this.cancel()
                    removeDim()
                    doSomething?.invoke()
                }
            }
        }

        maskAnimator.duration = sheetAnimator.duration
        maskAnimator.interpolator = sheetAnimator.interpolator
        dimAnimator.duration = sheetAnimator.duration
        dimAnimator.interpolator = sheetAnimator.interpolator

        AnimatorSet().apply {
            playTogether(sheetAnimator, dimAnimator, maskAnimator)
            start()
        }
    }

    override fun getIsDismissing(): Boolean {
        return true
    }

    override fun getHostFragment(): Fragment {
        return this
    }
}