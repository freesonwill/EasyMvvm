package com.cn.game.sdk2.ui.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.cn.game.sdk2.databinding.FragmentWinningAnimationBinding
import com.cn.game.sdk2.ui.helper.AnimHelper
import com.cn.game.sdk2.ui.page.fast3.Fast3MainFragment
import com.cn.game.sdk2.ui.viewmodel.WinningAnimationViewModel
import com.cn.game.sdk2.utils.tool.PromptSoundPlay
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import com.xcjh.base_lib2.utils.LogUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class WinningAnimationFragment: BaseFragment<WinningAnimationViewModel, FragmentWinningAnimationBinding>(){
    override val mBinding: FragmentWinningAnimationBinding by viewBind()
    override val mViewModel: WinningAnimationViewModel by viewModel()

    private var onAnimEndCallback: (() -> Unit)? = null
    private val lottieListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            Log.e(TAG, "groupWinLottie onAnimationStart")
            PromptSoundPlay.playWinEffect()

            mViewModel.isAnimating = true

            mBinding.apply {
                tvAnimWin2.alpha = 1f
                txtWinMoneyLabel.alpha = 1f

                lottieLayout.postDelayed({
                    AnimatorSet().apply {
                        playTogether(
                            listOf(
                                ObjectAnimator
                                    .ofFloat(tvAnimWin2, "alpha", 1f, 0f)
                                    .setDuration(1000),
                                ObjectAnimator
                                    .ofFloat(txtWinMoneyLabel, "alpha", 1f, 0f)
                                    .setDuration(1000)
                            )
                        )
                        start()
                    }
                }, 2500)
            }
        }

        override fun onAnimationEnd(animation: Animator) {
            handleAnimEnd(true)
        }

        override fun onAnimationCancel(animation: Animator) {
            handleAnimEnd()
        }

        override fun onAnimationRepeat(animation: Animator) {}
    }

    private val lottieAttachedChangeListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(p0: View) {}
        override fun onViewDetachedFromWindow(p0: View) {
            //groupWinLottie播发动画一半被window移除了，lottieListener不会执行onAnimationEnd，在这里执行
            LogUtils.wTag(Fast3MainFragment.TAG,"groupWinLottie is detached from window，isAnimating:${mViewModel.isAnimating}")

            if (mViewModel.isAnimating) {
                handleAnimEnd()
            }
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        mBinding.lottieAnimView.apply {
            addAnimatorListener(lottieListener)
            addOnAttachStateChangeListener(lottieAttachedChangeListener)
        }
    }

    override fun lazyLoadData() {
        // do nothing
    }

    override fun createObserver() {
        mViewModel.resultVisible.observe(viewLifecycleOwner) {
            mBinding.groupWinLottie.isVisible = it
        }
    }

    private fun handleAnimEnd(invokeCallback: Boolean = false) {
        if(invokeCallback) {
            onAnimEndCallback?.invoke()
        }
        
        with(mViewModel) {
            isAnimating = false
            setResultVisible(false)
        }
    }

    /**
     * 執行中獎動畫
     *
     * @param winMoney 中獎金額
     * @param endCallBack 動畫結束後執行
     */
    fun startWinLottieAnim(winMoney: Int, endCallBack: (() -> Unit)?) {
        onAnimEndCallback = endCallBack

        if(winMoney <= 0) {
            endCallBack?.invoke()
            return
        }

        mBinding.apply {
            Log.e(Fast3MainFragment.TAG, "本轮赢钱了--->$winMoney")

            mViewModel.setResultVisible(true)

            //groupWinLottie没在前台显示，不要做Lottie动画
            if (!groupWinLottie.isShown) {
                LogUtils.w("groupWinLottie is not shown at the front, ignore showLottie")
                handleAnimEnd()
                return
            }

            val duration = when {
                winMoney < 10_00 -> 400
                winMoney < 100_00 -> 500
                winMoney < 1000_00 -> 600
                winMoney <= 10000_00 -> 700
                else -> 1000L
            }
            AnimHelper.doNumberAnim(tvAnimWin2, 0, (winMoney).toLong(), duration)
            lottieAnimView.playAnimation()
            lottieAnimView2.playAnimation()
        }
    }

    companion object {
        const val TAG = "WinningAnimFragment"
    }
}