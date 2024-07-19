package com.cn.game.sdk2.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.cn.game.sdk2.R.id;
import com.cn.game.sdk2.R.layout;
import com.lxj.xpopup.animator.PopupAnimator;
import com.lxj.xpopup.animator.TranslateAnimator;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.enums.PopupStatus;
import com.lxj.xpopup.util.KeyboardUtils;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.SmartDragLayout;

public class CustomBottomPopupView extends BasePopupView {
    protected CustomSmartDragLayout bottomPopupContainer;
    private TranslateAnimator translateAnimator;

    public CustomBottomPopupView(@NonNull Context context) {
        super(context);
        this.bottomPopupContainer = this.findViewById(id.bottomPopupContainer);
    }

    protected void addInnerContent() {
        View contentView = LayoutInflater.from(this.getContext()).inflate(this.getImplLayoutId(), this.bottomPopupContainer, false);
        this.bottomPopupContainer.addView(contentView);
    }

    protected final int getInnerLayoutId() {
        return layout.xpopup_bottom_popup_view;
    }

    protected void initPopupContent() {
        super.initPopupContent();
        if (this.bottomPopupContainer.getChildCount() == 0) {
            this.addInnerContent();
        }

        this.bottomPopupContainer.setDuration(this.getAnimationDuration());
        this.bottomPopupContainer.enableDrag(CustomBottomPopupView.this.popupInfo.enableDrag);
        if (CustomBottomPopupView.this.popupInfo.enableDrag) {
            CustomBottomPopupView.this.popupInfo.popupAnimation = null;
            this.getPopupImplView().setTranslationX((float)CustomBottomPopupView.this.popupInfo.offsetX);
            this.getPopupImplView().setTranslationY((float)CustomBottomPopupView.this.popupInfo.offsetY);
        } else {
            this.getPopupContentView().setTranslationX((float)CustomBottomPopupView.this.popupInfo.offsetX);
            this.getPopupContentView().setTranslationY((float)CustomBottomPopupView.this.popupInfo.offsetY);
        }

        this.bottomPopupContainer.dismissOnTouchOutside(CustomBottomPopupView.this.popupInfo.isDismissOnTouchOutside);
        this.bottomPopupContainer.isThreeDrag(CustomBottomPopupView.this.popupInfo.isThreeDrag);
        XPopupUtils.applyPopupSize((ViewGroup)this.getPopupContentView(), this.getMaxWidth(), this.getMaxHeight(), this.getPopupWidth(), this.getPopupHeight(), (Runnable)null);
        this.bottomPopupContainer.setOnCloseListener(new SmartDragLayout.OnCloseListener() {
            public void onClose() {
                CustomBottomPopupView.this.beforeDismiss();
                if (CustomBottomPopupView.this.popupInfo != null && CustomBottomPopupView.this.popupInfo.xPopupCallback != null) {
                    CustomBottomPopupView.this.popupInfo.xPopupCallback.beforeDismiss(CustomBottomPopupView.this);
                }

                CustomBottomPopupView.this.doAfterDismiss();
            }

            public void onDrag(int value, float percent, boolean isScrollUp) {
                if (CustomBottomPopupView.this.popupInfo != null) {
                    if (CustomBottomPopupView.this.popupInfo.xPopupCallback != null) {
                        CustomBottomPopupView.this.popupInfo.xPopupCallback.onDrag(CustomBottomPopupView.this, value, percent, isScrollUp);
                    }

                    if (CustomBottomPopupView.this.popupInfo.hasShadowBg && !CustomBottomPopupView.this.popupInfo.hasBlurBg) {
                        CustomBottomPopupView.this.setBackgroundColor(CustomBottomPopupView.this.shadowBgAnimator.calculateBgColor(percent));
                    }

                }
            }

            public void onOpen() {
            }
        });
        this.bottomPopupContainer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CustomBottomPopupView.this.popupInfo != null) {
                    if (CustomBottomPopupView.this.popupInfo.xPopupCallback != null) {
                        CustomBottomPopupView.this.popupInfo.xPopupCallback.onClickOutside(CustomBottomPopupView.this);
                    }

                    if (CustomBottomPopupView.this.popupInfo.isDismissOnTouchOutside != null) {
                        CustomBottomPopupView.this.dismiss();
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
        if (CustomBottomPopupView.this.popupInfo != null) {
            if (CustomBottomPopupView.this.popupInfo.enableDrag) {
                if (CustomBottomPopupView.this.popupInfo.hasBlurBg && this.blurAnimator != null) {
                    this.blurAnimator.animateShow();
                }

                this.bottomPopupContainer.open();
            } else {
                super.doShowAnimation();
            }

        }
    }

    public void doDismissAnimation() {
        if (CustomBottomPopupView.this.popupInfo != null) {
            if (CustomBottomPopupView.this.popupInfo.enableDrag) {
                if (CustomBottomPopupView.this.popupInfo.hasBlurBg && this.blurAnimator != null) {
                    this.blurAnimator.animateDismiss();
                }

                this.bottomPopupContainer.close();
            } else {
                super.doDismissAnimation();
            }

        }
    }

    protected void doAfterDismiss() {
        if (CustomBottomPopupView.this.popupInfo != null) {
            if (CustomBottomPopupView.this.popupInfo.enableDrag) {
                if (CustomBottomPopupView.this.popupInfo.autoOpenSoftInput) {
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
        if (CustomBottomPopupView.this.popupInfo == null) {
            return null;
        } else {
            if (this.translateAnimator == null) {
                this.translateAnimator = new TranslateAnimator(this.getPopupContentView(), this.getAnimationDuration(), PopupAnimation.TranslateFromBottom);
            }

            return CustomBottomPopupView.this.popupInfo.enableDrag ? null : this.translateAnimator;
        }
    }

    public void dismiss() {
        if (CustomBottomPopupView.this.popupInfo != null) {
            if (CustomBottomPopupView.this.popupInfo.enableDrag) {
                if (this.popupStatus == PopupStatus.Dismissing) {
                    return;
                }

                this.popupStatus = PopupStatus.Dismissing;
                if (CustomBottomPopupView.this.popupInfo.autoOpenSoftInput) {
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
        if (CustomBottomPopupView.this.popupInfo != null && !CustomBottomPopupView.this.popupInfo.enableDrag && this.translateAnimator != null) {
            this.getPopupContentView().setTranslationX(this.translateAnimator.startTranslationX);
            this.getPopupContentView().setTranslationY(this.translateAnimator.startTranslationY);
            this.translateAnimator.hasInit = true;
        }

        super.onDetachedFromWindow();
    }
}
