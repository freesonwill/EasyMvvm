package arch.cayenne.module.chat.manager

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType

/**
 * @author: wenxi
 * @date: 18/11/25 14:52
 * @description:
 */
object SoftKeyBoardAnim {


    fun mainTransYAnim(offset: Int, main: ConstraintLayout): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(main, "translationY", offset.toFloat())
        anim.interpolator = FastOutSlowInInterpolator()
        return anim
    }

    fun ivLanguageAlphaAnim(isVisible: Boolean, ivLanguage: View): ObjectAnimator {
        val alphaObj = ObjectAnimator.ofFloat(ivLanguage, "alpha", if (isVisible) 1f else 0f)
        return alphaObj
    }

    /**
     * 输入框动画包含横移和缩放
     * */
    @SuppressLint("Recycle")
    fun chatEtInputAnim(
        scaleX: Float,
        transX: Float,
        chatEtInput: View,
    ): AnimatorSet {
        val animSet = AnimatorSet()
        chatEtInput.pivotX = 0.5f
        val scaleAnim = ObjectAnimator.ofFloat(chatEtInput, "scaleX", scaleX)
        val transXAnim = ObjectAnimator.ofFloat(chatEtInput, "translationX", transX)
        animSet.playTogether(scaleAnim, transXAnim)
        return animSet
    }


    //输入框在有内容和键盘弹出时的icon动画
    /**
     * @param isExpand true 上移 false 下移
     * */
    @SuppressLint("Recycle")
    fun inputIconAnim(
        isExpand: Boolean,
        ivAt: ImageView,
        ivBet: ImageView,
        ivEmoji: ImageView,
        ivLanguage: View
    ): Array<ObjectAnimator> {
        val atTransYParam = if (isExpand) 0f else 47.dp2px.toFloat()
        val atTransXParam = if (isExpand) 0f else 5.dp2px.toFloat()
        val betTransXParam = if (isExpand) 0f else 12.dp2px.toFloat()
        val emojiTransXParam = if (isExpand) 0f else 18.dp2px.toFloat()
        val languageAlphaParam = if (isExpand) 1f else 0f

        val scaleParam = if (isExpand) floatArrayOf(1.16f, 1f) else floatArrayOf(1f, 1.16f)

        val atTransXAnim = ObjectAnimator.ofFloat(ivAt, "translationX", atTransXParam)
        val atTransYAnim = ObjectAnimator.ofFloat(ivAt, "translationY", atTransYParam)
        val atScaleXParam = ObjectAnimator.ofFloat(ivAt, "scaleX", *scaleParam)
        val atScaleYParam = ObjectAnimator.ofFloat(ivAt, "scaleY", *scaleParam)

        val betTransXAnim = ObjectAnimator.ofFloat(ivBet, "translationX", betTransXParam)
        val betTransYAnim = ObjectAnimator.ofFloat(ivBet, "translationY", atTransYParam)
        val betScaleXParam = ObjectAnimator.ofFloat(ivBet, "scaleX", *scaleParam)
        val betScaleYParam = ObjectAnimator.ofFloat(ivBet, "scaleY", *scaleParam)

        val emojiTransXAnim =
            ObjectAnimator.ofFloat(ivEmoji, "translationX", emojiTransXParam)
        val emojiTransYAnim =
            ObjectAnimator.ofFloat(ivEmoji, "translationY", atTransYParam)
        val emojiScaleXParam = ObjectAnimator.ofFloat(ivEmoji, "scaleX", *scaleParam)
        val emojiScaleYParam = ObjectAnimator.ofFloat(ivEmoji, "scaleY", *scaleParam)

        val ivLanguageAlphaAnim = ObjectAnimator.ofFloat(ivLanguage, "alpha", languageAlphaParam)

        return arrayOf(
            atTransXAnim,
            atTransYAnim,
            atScaleXParam,
            atScaleYParam,
            betTransXAnim,
            betTransYAnim,
            betScaleXParam,
            betScaleYParam,
            emojiTransXAnim,
            emojiTransYAnim,
            emojiScaleXParam,
            emojiScaleYParam,
            ivLanguageAlphaAnim
        )
    }

    fun hotViewAnim(offset: Int, inputContent: ConstraintLayout) = ObjectAnimator.ofFloat(
        inputContent,
        "translationY",
        if (offset != 0) 0f else 44.dp2px.toFloat()
    ).apply {
        duration = 30
    }

    /***
     *输入框横移和缩放动画
     * 输入框按钮上下移动动画
     */
    @SuppressLint("Recycle")
    fun getInputAnim(
        actionType: KeyboardActionType,
        offset: Int,
        ivAt: ImageView,
        ivBet: ImageView,
        ivEmoji: ImageView,
        ivLanguage: View,
        chatLlInput: View
    ): AnimatorSet {
//                         没有弹出键盘 307(左边距：8 右边距: 10)  弹出键盘 355（左右边距:10）1.15  输入款有内容: 282(左边距：12,有边距：11) 0.91
//        transX                  0 (58)                           -48   (10)                        -46     (12)
        val btnAnim = AnimatorSet().apply {
            playTogether(
                *inputIconAnim(
                    offset == 0,
                    ivAt,
                    ivBet,
                    ivEmoji,
                    ivLanguage
                )
            )
        }
        val animSet = AnimatorSet()
        when (actionType) {
            KeyboardActionType.CHAT_TO_SOFT,
            KeyboardActionType.CHAT_TO_EMOJI -> { //弹出键盘 先按钮动画再做输入框动画
                val inputAnim =
                    SoftKeyBoardAnim.chatEtInputAnim(1.15f, -48.dp2px.toFloat(), chatLlInput)
                animSet.playSequentially(btnAnim, inputAnim)
            }

            KeyboardActionType.SOFT_TO_CHAT,
            KeyboardActionType.EMOJI_TO_CHAT -> {//收回键盘 先输入框动画恢复原位，再按钮动画
                val inputAnim = SoftKeyBoardAnim.chatEtInputAnim(1f, 0f, chatLlInput)
                animSet.playSequentially(inputAnim, btnAnim)
            }

            else -> {
                animSet.play(btnAnim)
            }
        }
        return animSet
    }

    //                         没有弹出键盘 307(左边距：8 右边距: 10)  弹出键盘 355（左右边距:10）1.15  输入款有内容: 282(左边距：12,有边距：11) 0.91
//        transX                  0 (58)                           -48   (10)                        -46     (12)
    @SuppressLint("Recycle")
    fun etInputContentAnim(
        isEmpty: Boolean,
        currentType: KeyBoardType,
        chatLlInput: View,
        tvSend: View
    ): AnimatorSet {
        var scaleX = 0f
        var transX = 0f
        when {
            isEmpty && currentType == KeyBoardType.CHAT -> {
                scaleX = 1f
                transX = 0f
            }

            isEmpty && currentType != KeyBoardType.CHAT -> {
                scaleX = 1.15f
                transX = -48.dp2px.toFloat()
            }

            else -> {
                scaleX = 0.91f
                transX = -46.dp2px.toFloat()
            }
        }
        val sendAlphaAnim = ObjectAnimator.ofFloat(tvSend, "alpha", if (isEmpty) 0f else 1f)
        val inputAnim = chatEtInputAnim(scaleX, transX, chatLlInput)
        val mainAnim = AnimatorSet().apply {
            if (isEmpty) playSequentially(sendAlphaAnim, inputAnim) else playSequentially(
                inputAnim,
                sendAlphaAnim
            )
        }

        return mainAnim
    }

}