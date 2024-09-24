package com.cn.game.sdk2.ui.xpopup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cn.game.sdk2.R
import com.lxj.xpopup.core.BasePopupView
import com.lxj.xpopup.enums.PopupStatus
import com.lxj.xpopup.util.KeyboardUtils
import com.lxj.xpopup.util.XPopupUtils

open class CustomPopupView(context: Context): BasePopupView(context) {

    private var customDragLayout: CustomDragLayout? = null

    init {
        customDragLayout = findViewById(R.id.bottomPopupContainer)
    }

    // 設置是否為遊戲主畫面
    fun setIsHome(isHome: Boolean) {
        customDragLayout?.setIsHome(isHome)
    }

    private fun addInnerContent() {
        val contentView = LayoutInflater.from(this.context)
            .inflate(
                implLayoutId,
                customDragLayout, false
            )
        customDragLayout?.addView(contentView)
    }

    override fun initPopupContent() {
        super.initPopupContent()

        customDragLayout?.apply {
            with(popupInfo) {
                if(childCount == 0) {
                    addInnerContent()
                }

                setDuration(animationDuration)
                enableDrag(enableDrag)
                if(enableDrag) {
                    popupAnimation = null
                    popupImplView.apply {
                        translationX = offsetX.toFloat()
                        translationY = offsetY.toFloat()
                    }
                } else {
                    popupContentView.apply {
                        translationX = offsetX.toFloat()
                        translationY = offsetY.toFloat()
                    }
                }

                dismissOnTouchOutside(isDismissOnTouchOutside)
                isThreeDrag(isThreeDrag)
                XPopupUtils.applyPopupSize(
                    popupContentView as ViewGroup,
                    maxWidth,
                    maxHeight,
                    popupWidth,
                    popupHeight, null as Runnable?
                )

                setOnStatusChangeListener(object: CustomDragLayout.OnStatusChangeListener{
                    override fun onOpen() {}

                    override fun onOpening() {
                        this@CustomPopupView.onOpening()
                    }

                    override fun onClose() {
                        beforeDismiss()
                        if (this@with != null && xPopupCallback != null) {
                            xPopupCallback.beforeDismiss(this@CustomPopupView)
                        }

                        doAfterDismiss()
                    }

                    override fun onClosing() {
                        this@CustomPopupView.onClosing()
                    }

                    override fun onDrag(y: Int, percent: Float, isScrollUp: Boolean) {
                        if (this@with != null) {
                            if (xPopupCallback != null) {
                                xPopupCallback.onDrag(
                                    this@CustomPopupView,
                                    y,
                                    percent,
                                    isScrollUp
                                )
                            }

                            if (hasShadowBg && !hasBlurBg) {
                                setBackgroundColor(shadowBgAnimator.calculateBgColor(percent))
                            }
                        }
                    }
                })

                setOnClickListener {
                    if (this@with != null) {
                        if (xPopupCallback != null) {
                            xPopupCallback.onClickOutside(this@CustomPopupView)
                        }

                        if (isDismissOnTouchOutside != null) {
                            dismiss()
                        }
                    }
                }
            }
        }
    }


    public override fun doShowAnimation() {
        if (popupInfo != null) {
            if (popupInfo.enableDrag) {
                if (popupInfo.hasBlurBg && this.blurAnimator != null) {
                    blurAnimator.animateShow()
                }

                customDragLayout?.open()
            } else {
                super.doShowAnimation()
            }
        }
    }

    public override fun doDismissAnimation() {
        if (popupInfo != null) {
            if (popupInfo.enableDrag) {
                if (popupInfo.hasBlurBg && this.blurAnimator != null) {
                    blurAnimator.animateDismiss()
                }

                customDragLayout?.close()
            } else {
                super.doDismissAnimation()
            }
        }
    }

    override fun dismiss() {
        if (popupInfo != null) {
            if (popupInfo.enableDrag) {
                if (popupStatus == PopupStatus.Dismissing) {
                    return
                }

                popupStatus = PopupStatus.Dismissing
                if (popupInfo.autoOpenSoftInput) {
                    KeyboardUtils.hideSoftInput(this)
                }

                clearFocus()
                customDragLayout?.close()
            } else {
                super.dismiss()
            }
        }
    }

    override fun getInnerLayoutId(): Int {
        return R.layout.xpopup_custom_popup_view
    }

    open fun onOpening() {}
    open fun onClosing() {}
}