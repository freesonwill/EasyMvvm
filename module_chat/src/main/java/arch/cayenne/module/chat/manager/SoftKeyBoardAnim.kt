package arch.cayenne.module.chat.manager

import android.animation.ObjectAnimator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px

/**
 * @author: wenxi
 * @date: 18/11/25 14:52
 * @description:
 */
object SoftKeyBoardAnim {


    fun mainTransYAnim(offset:Int,main:ConstraintLayout):ObjectAnimator{
        val anim = ObjectAnimator.ofFloat(main, "translationY", offset.toFloat())
        anim.interpolator = FastOutSlowInInterpolator()
        return anim
    }


    //输入框在有内容和键盘弹出时的icon动画
     fun inputIconAnim(isExpand: Boolean,ivAt:ImageView,ivBet:ImageView,ivEmoji:ImageView): Array<ObjectAnimator> {
        val atTransYParam = if (isExpand) 0f else 47.dp2px.toFloat()
        val atTransXParam = if (isExpand) 0f else 5.dp2px.toFloat()
        val betTransXParam = if (isExpand) 0f else 12.dp2px.toFloat()
        val emojiTransXParam = if (isExpand) 0f else 18.dp2px.toFloat()

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
            emojiScaleYParam
        )
    }

     fun hotViewAnim(offset: Int,inputContent:ConstraintLayout) = ObjectAnimator.ofFloat(
        inputContent,
        "translationY",
        if (offset != 0) 0f else 44.dp2px.toFloat()
    ).apply {
        duration = 30
    }


}