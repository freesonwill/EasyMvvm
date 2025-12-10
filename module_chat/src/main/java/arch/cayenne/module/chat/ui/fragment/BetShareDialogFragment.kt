package arch.cayenne.module.chat.ui.fragment

import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.common.utils.ViewUtils
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.databinding.FragmentBetShareDialogLayoutBinding
import arch.cayenne.module.chat.ui.viewmodel.BetShareViewModel
import kotlin.reflect.KClass

/**
 * @author: wenxi
 * @date: 8/12/25 14:52
 * @description:
 */
class BetShareDialogFragment :
    BasePreLoadBottomSheetFragment<BetShareViewModel, FragmentBetShareDialogLayoutBinding>() {
    override val vbClass: KClass<FragmentBetShareDialogLayoutBinding>
        get() = FragmentBetShareDialogLayoutBinding::class
    override val vmClass: KClass<BetShareViewModel>
        get() = BetShareViewModel::class
    private var betType: Int = 0 // 0 Game 1 Sport
    private var contentMaxHeight = 0
    private val sportMinHeight = 345.dp2px
    private val gameMinHeight = 425.dp2px
    private var heightAnim: ValueAnimator? = null

    companion object {
        val TAG = BetShareDialogFragment::class.java.simpleName

        fun create(fragment: Fragment): BetShareDialogFragment {
            var f = fragment.childFragmentManager.findFragmentByTag(TAG) as? BetShareDialogFragment
            if (f == null) {
                f = BetShareDialogFragment()
                f.customAttach(fragment, TAG)
            }
            return f
        }

        fun show(fragment: Fragment, betType: Int) {
            val betFragment =
                fragment.childFragmentManager.findFragmentByTag(TAG) as? BetShareDialogFragment
                    ?: create(fragment)
            betFragment.changeBetType(betType)
            betFragment.customShow()
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
//            setupFullScreen(dialog)
        }
        return dialog
    }

    /**
     * @param betType 0 Game 1 Sport
     * */
    fun changeBetType(betType: Int) {
        if (betType != 0 && betType != 1) {
            throw IllegalStateException("betType is Not 1 or 0")
        }
        this.betType = betType
        setFragmentUi()
        loadFragment()
    }

    private fun setFragmentUi() {
        mBinding.line.isVisible = betType == 1
        mBinding.clBottom.isVisible = betType == 1
        val lp = mBinding.fragmentContainer.layoutParams
        lp.height = if (betType == 0) gameMinHeight else sportMinHeight
        mBinding.fragmentContainer.layoutParams = lp
        contentMaxHeight =  if(betType == 0) gameMinHeight+269.dp2px else sportMinHeight+269.dp2px
    }


    override fun initView(savedInstanceState: Bundle?) {
    }


    override suspend fun createObserver() {
        super.createObserver()
        mViewModel.expandLiveData.observe(viewLifecycleOwner) {
            startHeightAnim()
        }
        mViewModel.closeLiveData.observe(viewLifecycleOwner) {
            dismiss()
        }
    }

    private fun loadFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            if (betType == 0) GameBetShareFragment() else SportBetShareFragment()
        ).commit()
    }

    override fun initListener() {

    }

    override fun onStart() {
        super.onStart()
    }


    override fun onStop() {
        super.onStop()
        if (heightAnim?.isRunning == true) {
            heightAnim?.cancel()
        }
    }


    private fun setupFullScreen(dialog: Dialog) {
        dialog.window?.apply {
            // 设置窗口标志
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
//            // 设置透明背景
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.height = 320.dp2px

        }
    }

    private fun startHeightAnim() {
        if (heightAnim?.isRunning == true) {
            heightAnim?.cancel()
        }
        mBinding.fragmentContainer.also { container ->
            val param = mutableListOf<Int>()
            if (container.height <= gameMinHeight) {
                param.add(if (betType == 0) gameMinHeight else sportMinHeight)
                param.add(contentMaxHeight)
            } else {
                param.add(contentMaxHeight)
                param.add(if (betType == 0) gameMinHeight else sportMinHeight)
            }
            heightAnim = ValueAnimator.ofInt(param[0], param[1]).apply {
                addUpdateListener { anim ->
                    val lp = container.layoutParams
                    lp.height = anim.animatedValue as Int
                    container.layoutParams = lp
                }
                duration = 200
                start()
            }
        }

    }
}