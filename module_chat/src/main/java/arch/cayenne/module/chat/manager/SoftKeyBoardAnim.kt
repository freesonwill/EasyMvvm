package arch.cayenne.module.chat.manager

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.base.utils.ext.LogUtilsExt.logd
import arch.cayenne.lib.common.data.constants.ChatMsgType
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.module.chat.R
import arch.cayenne.module.chat.data.constants.KeyBoardType
import arch.cayenne.module.chat.data.constants.KeyboardActionType
import arch.cayenne.module.chat.databinding.FragmentLiveChatBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import org.koin.java.KoinJavaComponent.getKoin

/**
 * @author: wenxi
 * @date: 18/11/25 14:52
 * @description: 软件盘切换 输入框切换 输入框按钮切换相关动画实现
 */
object SoftKeyBoardAnim {


    /**
     * 表情键盘的上下移动动画
     * */
    fun mainTransYAnim(offset: Int, main: ConstraintLayout): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(main, "translationY", offset.toFloat())
        anim.interpolator = FastOutSlowInInterpolator()
        return anim
    }


    /**
     * 输入框动画包含横移和缩放
     * */
    @SuppressLint("Recycle")
    fun chatEtInputAnim(
        targetWidth: Int,
        transX: Float,
        chatEtInput: View,
    ): AnimatorSet {
        val animSet = AnimatorSet()
        val d3pValue = targetWidth.dp2px
//        chatEtInput.pivotX = 0.5f
//        val scaleAnim = ObjectAnimator.ofInt(chatEtInput, "width", chatEtInput.width,scaleX)
        val inputWidthAnim = ValueAnimator.ofInt(chatEtInput.width, d3pValue).apply {
            addUpdateListener {
                val lp = chatEtInput.layoutParams
                lp.width = it.animatedValue as Int
                chatEtInput.layoutParams = lp
            }
        }
        val transXAnim = ObjectAnimator.ofFloat(chatEtInput, "translationX", transX)
        animSet.playTogether(inputWidthAnim, transXAnim)
        animSet.duration = etAnimDuration
        animSet.addListener(onEnd = {

        })
        return animSet
    }

    private val etAnimDuration = 20L

    /**
     * 输入框在有内容和键盘弹出时的at bet emoji language 按钮的动画
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
        val atTransYParam = if (isExpand) 0f else 48.dp2px.toFloat()
        val atTransXParam = if (isExpand) 0f else 5.dp2px.toFloat()
        val betTransXParam = if (isExpand) 0f else 11.dp2px.toFloat()
        val emojiTransXParam = if (isExpand) 0f else 16.dp2px.toFloat()

        val scaleParam = if (isExpand) floatArrayOf(1.16f, 1f) else floatArrayOf(1f, 1.16f)

//        val atTransXAnim = ObjectAnimator.ofFloat(ivAt, "translationX", atTransXParam)
//        val atTransYAnim = ObjectAnimator.ofFloat(ivAt, "translationY", atTransYParam)
//        val atScaleXParam = ObjectAnimator.ofFloat(ivAt, "scaleX", *scaleParam)
//        val atScaleYParam = ObjectAnimator.ofFloat(ivAt, "scaleY", *scaleParam)

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
        val languageAlphaParam = if (isExpand) 1f else 0f

        val ivLanguageAlphaAnim = ObjectAnimator.ofFloat(ivLanguage, "alpha", languageAlphaParam)


        return arrayOf(
//            atTransXAnim,
//            atTransYAnim,
//            atScaleXParam,
//            atScaleYParam,
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

//    fun hotViewAnim(offset: Int, inputContent: ConstraintLayout) = ObjectAnimator.ofFloat(
//        inputContent,
//        "translationY",
//        if (offset != 0) 0f else 44.dp2px.toFloat()
//    )


    /***
     *键盘弹出和收缩时，输入框横移和缩放 输入框按钮上下移动
     */
    @SuppressLint("Recycle")
    fun getInputAnim(
        actionType: KeyboardActionType,
        offset: Int,
        ivAt: ImageView,
        ivBet: ImageView,
        ivEmoji: ImageView,
        ivLanguage: View,
        chatLlInput: View,
        animStart: () -> Unit,
        animEnd: () -> Unit
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
            addListener(onStart = {
                animStart.invoke()
            }, onEnd = {
                animEnd.invoke()
            })
        }
        val animSet = AnimatorSet()
        when (actionType) {
            KeyboardActionType.CHAT_TO_SOFT,
            KeyboardActionType.CHAT_TO_EMOJI -> { //弹出键盘 先按钮动画再做输入框动画
                val inputAnim = chatEtInputAnim(355, -48.dp2px.toFloat(), chatLlInput)
                animSet.playTogether(btnAnim, inputAnim)
            }

            KeyboardActionType.SOFT_TO_CHAT,
            KeyboardActionType.EMOJI_TO_CHAT -> {//收回键盘 先输入框动画恢复原位，再按钮动画
                val inputAnim = chatEtInputAnim(307, 0f, chatLlInput)
                animSet.playTogether(inputAnim, btnAnim)
            }

            else -> {
                animSet.play(btnAnim)
            }
        }
        animSet.duration = etAnimDuration
        return animSet
    }

    /**
     * ChatEtInput长度和移动动画
     *      没有弹出键盘 307(左边距：8 右边距: 10)  弹出键盘 355（左右边距:10）1.15  输入款有内容: 282(左边距：12,有边距：11) 0.91
     * transX     0 (58)                           -48   (10)                        -46     (12)
     *
     *
     * **/
    @SuppressLint("Recycle")
    fun etInputContentAnim(
        isEmpty: Boolean,
        currentType: KeyBoardType,
        chatLlInput: View,
        tvSend: View
    ): AnimatorSet {
        var inputWidth = 0
        var transX = 0f
        when {
            isEmpty && currentType == KeyBoardType.CHAT -> {
                inputWidth = 307
                transX = 0f
            }

            isEmpty && currentType != KeyBoardType.CHAT -> {
                inputWidth = 355
                transX = -48.dp2px.toFloat()
            }

            else -> {
                inputWidth = 282
                transX = -46.dp2px.toFloat()
            }
        }
        val sendAlphaAnim = ObjectAnimator.ofFloat(tvSend, "alpha", if (isEmpty) 0f else 1f)
        val inputAnim = chatEtInputAnim(inputWidth, transX, chatLlInput)
        val mainAnim = AnimatorSet().apply {
//            if (isEmpty) playSequentially(sendAlphaAnim, inputAnim) else playSequentially(
//                inputAnim,
//                sendAlphaAnim
//            )
            playTogether(inputAnim, sendAlphaAnim)
            duration = etAnimDuration
        }

        return mainAnim
    }

    /**
     * 当ChatEtInput 内容有变化的时候调用
     * */
    fun etAnimWhenEtContentChange(
        binding: FragmentLiveChatBinding,
        currentType: KeyBoardType,
        onAnimStart: (value: Boolean) -> Unit,
        onAnimEnd: (value: Boolean) -> Unit
    ) {

        binding.apply {
            when {
                chatEtInput.length() == 0 && chatTvSend.isVisible && currentType != KeyBoardType.CHAT -> { //键盘弹出的时候发送 有内容到无内容
                    etInputContentAnim(
                        true,
                        currentType,
                        chatLlInput,
                        chatTvSend
                    ).apply {
                        duration = etAnimDuration
                        addListener(onStart = {
                        }, onEnd = {
                            chatTvSend.isVisible = false
                        })
                        start()
                    }
                }

                chatEtInput.length() == 0 && chatTvSend.isVisible && currentType == KeyBoardType.CHAT -> {//键盘收缩的时候发送，有内容到无内容
//                    input输入框扩展 -> 按钮动画
                    val btnAnim = AnimatorSet().apply {
                        playTogether(
                            *inputIconAnim(
                                true,
                                ivAt,
                                ivBet,
                                ivEmoji,
                                ivLanguage
                            )
                        )
                        addListener(onStart = {
                            onAnimStart.invoke(true)
                        })
                    }
                    val inputAnim = etInputContentAnim(
                        true,
                        currentType,
                        chatLlInput,
                        chatTvSend
                    ).apply {
                        addListener(onEnd = {
                            chatTvSend.isVisible = false
                        })
                    }
                    AnimatorSet().apply {
                        duration = etAnimDuration
                        playSequentially(inputAnim, btnAnim)
                        start()
                    }

                }

                chatEtInput.length() > 0 && !chatTvSend.isVisible -> { //无内容到有内容
                    if (ivLanguage.isVisible) {
                        addBetToEtInputAnim(
                            binding,
                            currentType,
                            onAnimStart = {
//                                onAnimStart.invoke(true)
                            },
                            onAnimEnd = { onAnimEnd.invoke(false) }
                        )
                    } else {
                        etInputContentAnim(
                            false,
                            currentType,
                            chatLlInput,
                            chatTvSend
                        ).apply {
                            duration = etAnimDuration
                            addListener(onStart = {
                                chatTvSend.isVisible = true
//                                onAnimStart.invoke(true)
                            }, onEnd = {
                                onAnimEnd.invoke(false)
                            })
                            start()
                        }
                    }
                }

                else -> {

                }
            }
        }

    }

    /**
     * 当前状态为chat时，向输入框插入字符串时输入框的动画
     * **/
    fun addBetToEtInputAnim(
        binding: FragmentLiveChatBinding,
        currentType: KeyBoardType,
        onAnimStart: () -> Unit,
        onAnimEnd: () -> Unit
    ) {
        binding.apply {
            val btnAnim = AnimatorSet().apply {
                playTogether(
                    *inputIconAnim(
                        false,
                        ivAt,
                        ivBet,
                        ivEmoji,
                        ivLanguage
                    )
                )
                addListener(onStart = {
//                    updateInputIcon(true)
                    onAnimStart.invoke()
                }, onEnd = {
//                    updateInputIcon(false)
                    onAnimEnd.invoke()
                })
            }
            val inputEtAnim =
                etInputContentAnim(false, currentType, chatLlInput, chatTvSend)
            val animSet = AnimatorSet()
            animSet.playSequentially(btnAnim, inputEtAnim)
            animSet.duration = etAnimDuration
            animSet.addListener(onStart = {
                chatTvSend.isVisible = true
            })
            animSet.start()
        }
    }

    /**
     * 收到at消息时，item的闪烁动画
     * */
    fun atFlashNotifyAnim(targetView: View,msgType:ChatMsgType): ObjectAnimator {
        val pathinterpolator = PathInterpolator(0.22f, 1f, 0.36f, 1f)
        val flashColor = ContextCompat.getColorStateList(
            targetView.context,
            arch.cayenne.lib.common.R.color.color_FFFFFF
        )
        val originColor =if(msgType in arrayOf(ChatMsgType.BET_SPORT,ChatMsgType.BET_GAME))
            ContextCompat.getColorStateList(
                targetView.context,
                arch.cayenne.lib.common.R.color.color_632433
            )else  ContextCompat.getColorStateList(
            targetView.context,
            arch.cayenne.lib.common.R.color.color_0FFFFFFF
        )
        val anim = ObjectAnimator.ofFloat(targetView, "alpha", 0.22f, 1f, 0.36f, 1f).apply {
            duration = 1000L
//            interpolator = pathinterpolator
            startDelay = 500L
            addListener(onStart = {
                targetView.backgroundTintList = flashColor
//                targetView.postDelayed({
//                    targetView.backgroundTintList = originColor
//                }, 1500-50)
            }, onEnd = {
                targetView.backgroundTintList = originColor
            })
            start()
        }
        return anim
    }
}