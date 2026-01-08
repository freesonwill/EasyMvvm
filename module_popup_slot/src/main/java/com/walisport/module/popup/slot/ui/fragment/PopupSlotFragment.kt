package com.walisport.module.popup.slot.ui.fragment

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import arch.cayenne.lib.base.ui.animation.CustomCurveTransformer
import arch.cayenne.lib.base.ui.fragment.BaseFragment
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.NavigationExt.navigate
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import arch.cayenne.lib.common.utils.ext.startSafeAnimateSet
import com.walisport.module.popup.slot.R
import com.walisport.module.popup.slot.data.PopupSlotDataModel
import com.walisport.module.popup.slot.databinding.FragmentPopupSlotBinding
import com.walisport.module.popup.slot.ui.adapter.BannerUrlImageAdapter
import com.walisport.module.popup.slot.ui.viewmodel.PopUpSlotViewModel
import kotlin.reflect.KClass

/**
 *
 * @date: 2025/12/5 19:23
 * @description:
 */
class PopupSlotFragment :
    BaseFragment<PopUpSlotViewModel, FragmentPopupSlotBinding>() {
    override val vbClass: KClass<FragmentPopupSlotBinding> =
        FragmentPopupSlotBinding::class
    override val vmClass: KClass<PopUpSlotViewModel> = PopUpSlotViewModel::class


    override fun initView(savedInstanceState: Bundle?) {
        val radius = 6.dp2px.toFloat()
        with(mBinding.popupSlot0.binding) {
            vpBanner.setBannerRound(radius)
            vpBanner.isAutoLoop(false)
            // 设置滑动时长丝滑,不影响曲线,
            vpBanner.setScrollTime(600)  // 0.5 秒
            vpBanner.setPageTransformer(CustomCurveTransformer())
            // 启动轮播
        }

        with(mBinding.popupSlot1.binding) {
            vpBanner.setBannerRound(radius)
            vpBanner.isAutoLoop(false)
            // 设置滑动时长丝滑,不影响曲线,
            vpBanner.setScrollTime(600)  // 0.5 秒
            vpBanner.setPageTransformer(CustomCurveTransformer())
            // 启动轮播
        }
    }

    override fun initListener() {
        mBinding.popupSlot0.setPerformClick {

        }

        mBinding.popupSlot0.binding.ivPopupSlotClose.clickNoRepeat {
            mViewModel.hideView(0)
        }

        mBinding.popupSlot1.setPerformClick { }

        mBinding.popupSlot1.binding.ivPopupSlotClose.clickNoRepeat {
            mViewModel.hideView(1)
        }
    }

    override suspend fun createObserver() {
        //监听弹窗显示与隐藏
        mViewModel.popupShowLiveData.observe(viewLifecycleOwner) {
            val showList = it
            val initialAlpha = 0.5f
            val targetAlpha = 1f
            val alphaDuration = 320L


            // 假设 PopupSlotRepository 有两个布尔变量 slot0Animated 和 slot1Animated
            if (showList.isNotEmpty() && showList[0]) {
                mBinding.popupSlot0.visibility = View.VISIBLE
                if (!mViewModel.slot0Animated) {
                    mBinding.popupSlot0.apply {
                        alpha = initialAlpha
                        animate().alpha(targetAlpha).setDuration(alphaDuration).start()
                        ObjectAnimator.ofFloat(this, "rotation", -10f, 10f, -10f, 10f, 0f).apply {
                            duration = 500 // 总动画时长，可根据需要调整
                            repeatCount = 0
                            repeatMode = ValueAnimator.RESTART
                            start()
                        }
                    }
                    mViewModel.slot0Animated = true
                }
            } else {
                mBinding.popupSlot0.visibility = View.GONE
            }

            if (showList.size > 1 && showList[1]) {
                mBinding.popupSlot1.visibility = View.VISIBLE
                if (!mViewModel.slot1Animated) {
                    mBinding.popupSlot1.apply {
                        alpha = initialAlpha
                        animate().alpha(targetAlpha).setDuration(alphaDuration).start()
                        ObjectAnimator.ofFloat(this, "rotation", -10f, 10f, -10f, 10f, 0f).apply {
                            duration = 500 // 总动画时长，可根据需要调整
                            repeatCount = 0
                            repeatMode = ValueAnimator.RESTART
                            start()
                        }
                    }
                    mViewModel.slot1Animated = true
                }
            } else {
                mBinding.popupSlot1.visibility = View.GONE
            }
        }

        mViewModel.popupSlotDataListLiveData.observe(viewLifecycleOwner) {
            val dataList = it
            // 可根据需要将此变量提到类属性或通过参数传递
            if (dataList.isNotEmpty()) {
                mBinding.popupSlot0.binding.vpBanner.apply {
                    setAdapter(BannerUrlImageAdapter(dataList[0].data.map {
                        Pair(it, R.drawable.popup_slot_placeholder)
                    }))
                    setLoopTime(LOOP_TIME)
                    isAutoLoop(true)
                    setOnBannerListener { data, position ->
                        // 这里处理点击事件，比如：
                        val url =
                            ((data as Pair<PopupSlotDataModel, Int>).first as PopupSlotDataModel).operateParams[0]
                        navigate(
                            arch.cayenne.lib.res.R.string.nav_module_web_fragment
                                .deeplink("outerSite" to true, "url" to url)
                        )

//                        val intent = android.content.Intent(
//                            android.content.Intent.ACTION_VIEW,
//                            android.net.Uri.parse(url)
//                        )
//                        startActivity(intent)
                    }
                    start()

                }
            }

            if (dataList.size > 1) {
                mBinding.popupSlot1.binding.vpBanner.apply {
                    setAdapter(BannerUrlImageAdapter(dataList[1].data.map {
                        Pair(
                            it,
                            R.drawable.popup_slot_placeholder
                        )
                    }))
                    setLoopTime(LOOP_TIME)
                    isAutoLoop(true)
                    setOnBannerListener { data, position ->
                        // 这里处理点击事件，比如：
                        val url =
                            ((data as Pair<PopupSlotDataModel, Int>).first as PopupSlotDataModel).operateParams[0]
                        navigate(
                            arch.cayenne.lib.res.R.string.nav_module_web_fragment
                                .deeplink("outerSite" to true, "url" to url)
                        )

//                        val intent = android.content.Intent(
//                            android.content.Intent.ACTION_VIEW,
//                            android.net.Uri.parse(url)
//                        )
//                        startActivity(intent)
                    }
                    start()
                }
            }
        }
    }

    var fadeOutAnimator: Animator? = null
    var fadeInAnimator: Animator? = null

    fun fadeAndOut() {
        mBinding.popupSlot0.binding.vpBanner.stop()
        mBinding.popupSlot1.binding.vpBanner.stop()
        fadeInAnimator?.cancel()
        fadeOutAnimator?.cancel()

        fadeOutAnimator = view?.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofFloat(
                    view?.translationX ?: 0f,
                    27.dp2px.toFloat()
                ).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        view?.translationX = value
                    }
                },
                ValueAnimator.ofFloat(
                    view?.alpha ?: 1f,
                    0.6f
                ).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        view?.alpha = value
                    }
                },
                ValueAnimator.ofFloat(mBinding.popupSlot0.binding.ivPopupSlotClose.alpha, 0f)
                    .apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            mBinding.popupSlot0.binding.ivPopupSlotClose.alpha = value
                            mBinding.popupSlot1.binding.ivPopupSlotClose.alpha = value
                        }
                    },
                ValueAnimator.ofFloat(mBinding.popupSlot1.binding.ivPopupSlotClose.alpha, 0f)
                    .apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            mBinding.popupSlot1.binding.ivPopupSlotClose.alpha = value
                        }
                    }
            )
        }, duration = 150, interpolator = LinearInterpolator(), start = true)

    }

    fun fadeAndIn() {
        mBinding.popupSlot0.binding.vpBanner.start()
        mBinding.popupSlot1.binding.vpBanner.start()
        fadeInAnimator?.cancel()
        fadeOutAnimator?.cancel()

        fadeInAnimator = view?.startSafeAnimateSet({
            playTogether(
                ValueAnimator.ofFloat(
                    view?.translationX ?: 27.dp2px.toFloat(),
                    0f
                ).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        view?.translationX = value
                    }
                },
                ValueAnimator.ofFloat(
                    view?.alpha ?: 0.6f,
                    1f
                ).apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        view?.alpha = value
                    }
                },
                ValueAnimator.ofFloat(mBinding.popupSlot0.binding.ivPopupSlotClose.alpha, 1f)
                    .apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            mBinding.popupSlot0.binding.ivPopupSlotClose.alpha = value
                            mBinding.popupSlot1.binding.ivPopupSlotClose.alpha = value
                        }
                    },
                ValueAnimator.ofFloat(mBinding.popupSlot1.binding.ivPopupSlotClose.alpha, 1f)
                    .apply {
                        addUpdateListener {
                            val value = it.animatedValue as Float
                            mBinding.popupSlot1.binding.ivPopupSlotClose.alpha = value
                        }
                    }
            )
        }, duration = 150, interpolator = LinearInterpolator(), start = true)
    }


    companion object {
        const val TAG = "PopupSlotFragment"
        const val LOOP_TIME = 3000L

    }


}





