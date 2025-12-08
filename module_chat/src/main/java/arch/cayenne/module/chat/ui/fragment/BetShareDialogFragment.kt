package arch.cayenne.module.chat.ui.fragment

import android.animation.ValueAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import arch.cayenne.lib.base.ui.fragment.BasePreLoadBottomSheetFragment
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
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
    private var dialogBetMaxHeight: Int = 0
    private val minHeight = 425.dp2px

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
        loadFragment()
    }

    private fun loadFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            if (betType == 0) GameShareFragment() else SportShareFragment()
        ).commit()
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

    override fun initView(savedInstanceState: Bundle?) {
        "BetShareFragment ${mViewModel}".logd("aaa")
        setMaxHeight()
    }


    private fun setMaxHeight() {
        dialogBetMaxHeight = requireContext().resources.displayMetrics.heightPixels - 118.dp2px
    }

    override fun initListener() {
        mBinding.apply {
            ivExpand.clickNoRepeat {
                startHeightAnim()
            }
            ivClose.clickNoRepeat {
                dismiss()
            }
        }
    }

    var heightAnim: ValueAnimator? = null
    private fun startHeightAnim() {

        "setHeightAnim ${mBinding.content.height}  minHeight ${minHeight}  dialogBetMaxHeight $dialogBetMaxHeight".logd(
            "aaa"
        )
        if (heightAnim?.isRunning == true) {
            heightAnim?.cancel()
        }
        val param = if (mBinding.content.height == minHeight) arrayOf(
            minHeight,
            dialogBetMaxHeight
        ) else arrayOf(dialogBetMaxHeight, minHeight)
        heightAnim = ValueAnimator.ofInt(param[0], param[1]).apply {
            duration = 200
            addUpdateListener {
                val lp = mBinding.content.layoutParams
                lp.height = it.animatedValue as Int
                mBinding.content.layoutParams = lp
            }
            start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (heightAnim?.isRunning == true) {
            heightAnim?.cancel()
        }
    }

}