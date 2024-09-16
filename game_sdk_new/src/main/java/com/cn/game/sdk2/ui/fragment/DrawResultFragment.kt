package com.cn.game.sdk2.ui.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.cn.game.sdk2.R
import com.cn.game.sdk2.databinding.FragmentDrawResultBinding
import com.cn.game.sdk2.ui.viewmodel.DrawResultViewModel
import com.cn.game.sdk2.utils.IconUtils
import com.cn.game.sdk2.utils.ext.BizExt.isLeopard
import com.cn.game.sdk2.utils.ext.CommonExt.toPinyin
import com.cn.game.sdk2.utils.ext.ViewExt.getCenterPoint
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.xcjh.base_lib2.base.fragment.BaseFragment
import com.xcjh.base_lib2.base.fragment.viewBind
import org.koin.androidx.viewmodel.ext.android.viewModel

class DrawResultFragment: BaseFragment<DrawResultViewModel, FragmentDrawResultBinding>() {
    override val mBinding: FragmentDrawResultBinding by viewBind()
    override val mViewModel: DrawResultViewModel by viewModel()

    override fun initView(savedInstanceState: Bundle?) {
        // do nothing
    }

    override fun lazyLoadData() {
        // do nothing
    }

    override fun createObserver() {
        // 更新当局游戏结果信息
        mViewModel.drawResult.observe(viewLifecycleOwner) {
            with(mBinding) {
                it.performs.forEachIndexed { index, item ->
                    val id = IconUtils.getIcon("game_sdk_icon_dice_" + item.toPinyin())
                    if (id != 0) {
                        when (index) {
                            0 -> ivDrawYi.setImageResource(id)
                            1 -> ivDrawEr.setImageResource(id)
                            2 -> ivDrawSan.setImageResource(id)
                        }
                    }
                }
                txtHomeTotal.text = it.sum.toString()

                //豹子只显示骰子和点数，不显示大小和单双
                if (it.isLeopard) {
                    ivBetSize.isVisible = false
                    ivBetOdd.isVisible = false
                } else {
                    ivBetSize.isVisible = true
                    ivBetOdd.isVisible = true

                    ivBetSize.setImageResource(
                        if (it.isBig) R.mipmap.game_sdk_icon_home_result_big
                        else R.mipmap.game_sdk_icon_home_result_small
                    )
                    ivBetOdd.setImageResource(
                        if (it.isDouble) R.mipmap.game_sdk_icon_home_result_double
                        else R.mipmap.game_sdk_icon_home_result_single
                    )
                }
            }
        }
    }

    /**
     * 設定當局開獎結果
     *
     * @param result 開獎結果
     */
    fun setDrawResult(result: RoundInfoBean) {
        mViewModel.setDrawResult(result)
    }

    /**
     * 執行動畫
     * 规格: 左侧动画 位移和放缩动画同时进行,scale 0.1->1 执行250ms; 右侧动画:间隔左侧动画750ms, scale 0.1->1 执行200ms
     * @param duration 動畫時間，預設為200L
     * @param doEnd 動畫結束後執行
     */
    fun playAnim(startPosition: Point, duration: Long = 200L, doEnd: () -> Unit) {
        mBinding.apply {
            val scaleProperties = listOf(
                Triple("scaleX", 0.1f, 1f),
                Triple("scaleY", 0.1f, 1f)
            )
            val leftPositionProperties = mutableListOf<Triple<String, Float, Float>>().apply {
                val point = llResultLeft.getCenterPoint()

                add(Triple("translationX", (startPosition.x - point.x).toFloat(), 0f))
                add(Triple("translationY", (startPosition.y - point.y).toFloat(), 0f))
            }

            val leftAnim = AnimatorSet().apply {
                val anim = leftPositionProperties.apply { addAll(scaleProperties) }.map { property ->
                    ObjectAnimator.ofFloat(llResultLeft, property.first, property.second, property.third)
                        .apply {
                            this.duration = 250
                        }
                }
                playTogether(anim)
            }

            val rightAnim = AnimatorSet().apply {
                val anim = scaleProperties.map { property ->
                    ObjectAnimator.ofFloat(llResultRight,property.first, property.second, property.third)
                        .apply {
                            this.duration = 400
                            startDelay = 500+250 //延迟500ms,但是为了跟ios同步,这里加上250偏移
                            interpolator = OvershootInterpolator(2f)
                        }
                }
                playTogether(anim)
            }
            AnimatorSet().apply {
                playSequentially(leftAnim , rightAnim)
                addListener(
                    onStart = { //设置为0f是为了隐藏view
                        llResultLeft.scaleX = 0f
                        llResultLeft.scaleY = 0f
                        llResultRight.scaleX = 0f
                        llResultRight.scaleY = 0f
                    },
                    onEnd = { doEnd.invoke() }
                )
                start()
            }
        }
    }

    companion object {
        const val TAG = "DrawResultFragment"
    }
}