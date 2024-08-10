package com.cn.game.sdk2.ui.popup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.cn.game.sdk2.R.id;
import com.cn.game.sdk2.R.layout;
import com.cn.game.sdk2.ui.view.VerticalSmartDragLayout;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.animator.TranslateAnimator;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupStatus;
import com.lxj.xpopup.util.KeyboardUtils;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.SmartDragLayout;

public class VerticalBottomPopupView extends BasePopupView {
    protected VerticalSmartDragLayout bottomPopupContainer;
    private TranslateAnimator translateAnimator;

    public VerticalBottomPopupView(@NonNull Context context) {
        super(context);
        this.bottomPopupContainer = this.findViewById(id.bottomPopupContainer);
    }

    protected void addInnerContent() {
        View contentView = LayoutInflater.from(this.getContext()).inflate(this.getImplLayoutId(), this.bottomPopupContainer, false);
        this.bottomPopupContainer.addView(contentView);
    }

    protected final int getInnerLayoutId() {
        return layout.xpopup_bottom_popup_vertical_view;
    }

    protected void initPopupContent() {
        super.initPopupContent();
        if (this.bottomPopupContainer.getChildCount() == 0) {
            this.addInnerContent();
        }

        this.bottomPopupContainer.setDuration(this.getAnimationDuration());
        this.bottomPopupContainer.enableDrag(VerticalBottomPopupView.this.popupInfo.enableDrag);
        if (VerticalBottomPopupView.this.popupInfo.enableDrag) {
            VerticalBottomPopupView.this.popupInfo.popupAnimation = null;
            this.getPopupImplView().setTranslationX((float) VerticalBottomPopupView.this.popupInfo.offsetX);
            this.getPopupImplView().setTranslationY((float) VerticalBottomPopupView.this.popupInfo.offsetY);
        } else {
            this.getPopupContentView().setTranslationX((float) VerticalBottomPopupView.this.popupInfo.offsetX);
            this.getPopupContentView().setTranslationY((float) VerticalBottomPopupView.this.popupInfo.offsetY);
        }

        this.bottomPopupContainer.dismissOnTouchOutside(VerticalBottomPopupView.this.popupInfo.isDismissOnTouchOutside);
        this.bottomPopupContainer.isThreeDrag(VerticalBottomPopupView.this.popupInfo.isThreeDrag);
        XPopupUtils.applyPopupSize((ViewGroup)this.getPopupContentView(), this.getMaxWidth(), this.getMaxHeight(), this.getPopupWidth(), this.getPopupHeight(), (Runnable)null);
        this.bottomPopupContainer.setOnCloseListener(new SmartDragLayout.OnCloseListener() {
            public void onClose() {
                VerticalBottomPopupView.this.beforeDismiss();
                if (VerticalBottomPopupView.this.popupInfo != null && VerticalBottomPopupView.this.popupInfo.xPopupCallback != null) {
                    VerticalBottomPopupView.this.popupInfo.xPopupCallback.beforeDismiss(VerticalBottomPopupView.this);
                }

                VerticalBottomPopupView.this.doAfterDismiss();
            }

            public void onDrag(int value, float percent, boolean isScrollUp) {
                if (VerticalBottomPopupView.this.popupInfo != null) {
                    if (VerticalBottomPopupView.this.popupInfo.xPopupCallback != null) {
                        VerticalBottomPopupView.this.popupInfo.xPopupCallback.onDrag(VerticalBottomPopupView.this, value, percent, isScrollUp);
                    }

                    if (VerticalBottomPopupView.this.popupInfo.hasShadowBg && !VerticalBottomPopupView.this.popupInfo.hasBlurBg) {
                        VerticalBottomPopupView.this.setBackgroundColor(VerticalBottomPopupView.this.shadowBgAnimator.calculateBgColor(percent));
                    }

                }
            }

            public void onOpen() {
            }
        });
        this.bottomPopupContainer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (VerticalBottomPopupView.this.popupInfo != null) {
                    if (VerticalBottomPopupView.this.popupInfo.xPopupCallback != null) {
                        VerticalBottomPopupView.this.popupInfo.xPopupCallback.onClickOutside(VerticalBottomPopupView.this);
                    }

                    if (VerticalBottomPopupView.this.popupInfo.isDismissOnTouchOutside != null) {
                        VerticalBottomPopupView.this.dismiss();
                    }
                }

            }
        });
    }

    protected void doMeasure() {
        super.doMeasure();
        XPopupUtils.applyPopupSize((ViewGroup)this.getPopupContentView(), this.getMaxWidth(), this.getMaxHeight(), this.getPopupWidth(), this.getPopupHeight(), (Runnable)null);
    }

    public void doShowAnimation() {
        if (VerticalBottomPopupView.this.popupInfo != null) {
            if (VerticalBottomPopupView.this.popupInfo.enableDrag) {
                if (VerticalBottomPopupView.this.popupInfo.hasBlurBg && this.blurAnimator != null) {
                    this.blurAnimator.animateShow();
                }

                this.bottomPopupContainer.open();
            } else {
                super.doShowAnimation();
            }

        }
    }

    public void doDismissAnimation() {
        if (VerticalBottomPopupView.this.popupInfo != null) {
            if (VerticalBottomPopupView.this.popupInfo.enableDrag) {
                if (VerticalBottomPopupView.this.popupInfo.hasBlurBg && this.blurAnimator != null) {
                    this.blurAnimator.animateDismiss();
                }

                this.bottomPopupContainer.close();
            } else {
                super.doDismissAnimation();
            }

        }
    }

    protected void doAfterDismiss() {
        if (VerticalBottomPopupView.this.popupInfo != null) {
            if (VerticalBottomPopupView.this.popupInfo.enableDrag) {
                if (VerticalBottomPopupView.this.popupInfo.autoOpenSoftInput) {
                    KeyboardUtils.hideSoftInput(this);
                }

                this.handler.removeCallbacks(this.doAfterDismissTask);
                this.handler.postDelayed(this.doAfterDismissTask, 0L);
            } else {
                super.doAfterDismiss();
            }

        }
    }

    protected PopupAnimator getPopupAnimator() {
        if (VerticalBottomPopupView.this.popupInfo == null) {
            return null;
        } else {
            if (this.translateAnimator == null) {
                this.translateAnimator = new TranslateAnimator(this.getPopupContentView(), this.getAnimationDuration(), PopupAnimation.TranslateFromBottom);
            }

            return VerticalBottomPopupView.this.popupInfo.enableDrag ? null : this.translateAnimator;
        }
    }

    public void dismiss() {
        if (VerticalBottomPopupView.this.popupInfo != null) {
            if (VerticalBottomPopupView.this.popupInfo.enableDrag) {
                if (this.popupStatus == PopupStatus.Dismissing) {
                    return;
                }

                this.popupStatus = PopupStatus.Dismissing;
                if (VerticalBottomPopupView.this.popupInfo.autoOpenSoftInput) {
                    KeyboardUtils.hideSoftInput(this);
                }

                this.clearFocus();
                this.bottomPopupContainer.close();
            } else {
                super.dismiss();
            }

        }
    }

    protected int getImplLayoutId() {
        return 0;
    }

    protected void onDetachedFromWindow() {
        if (VerticalBottomPopupView.this.popupInfo != null && !VerticalBottomPopupView.this.popupInfo.enableDrag && this.translateAnimator != null) {
            this.getPopupContentView().setTranslationX(this.translateAnimator.startTranslationX);
            this.getPopupContentView().setTranslationY(this.translateAnimator.startTranslationY);
            this.translateAnimator.hasInit = true;
        }

        super.onDetachedFromWindow();
    }
}
