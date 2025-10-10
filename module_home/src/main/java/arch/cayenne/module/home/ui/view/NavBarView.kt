package arch.cayenne.module.home.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getColor
import arch.cayenne.lib.skin.widget.SkinnableConstraintLayout
import arch.cayenne.module.home.R
import arch.cayenne.module.home.databinding.ItemNavbarTempleBinding
import kotlin.properties.Delegates

/**
 * @date: 2025/10/2 14:53
 * @description:
 */
class NavBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkinnableConstraintLayout(context,attrs,defStyleAttr) {
    private var barStyle:Style? = null
    private val binding:ItemNavbarTempleBinding = ItemNavbarTempleBinding.inflate(LayoutInflater.from(context), this)
    private var badgeMarginStartDefault by Delegates.notNull<Int>()
    private val badgeVisible = 0
    private val badgeGone = 1

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.NavBarViewStyle,
            defStyleAttr,
            0
        ).let { ta ->
            try {
                ta.getDrawable(R.styleable.NavBarViewStyle_icon)?.apply {
                    binding.icon.apply { isVisible = true }.setImageDrawable(this)
                }
                ta.getString(R.styleable.NavBarViewStyle_text)?.apply {
                    binding.text.apply { isVisible = true }.text = this
                }
                val badgeShow =  ta.getInt(R.styleable.NavBarViewStyle_badge_visibility,badgeGone) == badgeVisible
                binding.badge.isVisible = badgeShow
                if(badgeShow){
                    ta.getString(R.styleable.NavBarViewStyle_badge)?.apply {
                        binding.badge.apply {
                            val badgeColor = ta.getColor(R.styleable.NavBarViewStyle_badge_color, arch.cayenne.module.bet.R.color.red.getColor(context))
                            val badgeRadius = ta.getDimension(R.styleable.NavBarViewStyle_badge_radius, 8f.dp2px.toFloat())
                            val circleDrawable = GradientDrawable().apply {
                                shape = GradientDrawable.RECTANGLE
                                cornerRadius = badgeRadius
                                setColor(badgeColor)
                            }
                            setBackgroundDrawable(circleDrawable)
                        }.text = this

                    }
                }



            } finally {
                ta.recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onInit()
    }

    private fun onInit(){
        (binding.badge.layoutParams as MarginLayoutParams).apply {
            badgeMarginStartDefault = marginStart
        }
    }

    fun getBarStyle():Class<out Style> {
        if(binding.run { icon.isVisible && text.isVisible &&  badge.isVisible }) return Style.IconTextBadge::class.java
        if(binding.run { icon.isVisible && text.isVisible}) return Style.IconText::class.java
        if(binding.run { icon.isVisible && badge.isVisible}) return Style.IconBadge::class.java
        if(binding.run { icon.isVisible}) return Style.Icon::class.java
        throw IllegalStateException("Illegal style")
    }

    fun setBarStyle(s:Style){
        this.barStyle = s
        (binding.root as ViewGroup).forEach { it.isVisible = false }
        when(s){
            is Style.Icon -> {
                binding.icon.apply { isVisible = true; setImageDrawable(s.icon) }
            }
            is Style.IconText -> {
                binding.icon.apply { isVisible = true; setImageDrawable(s.icon) }
                binding.text.apply { isVisible = true; text = s.text }
            }
            is Style.IconTextBadge -> {
                binding.icon.apply { isVisible = true; setImageDrawable(s.icon) }
                binding.text.apply { isVisible = true; text = s.text }
                binding.badge.apply { isVisible = true; text = s.badge }
                (s.badgeMarginStart ?: badgeMarginStartDefault).let {
                    binding.badge.updateLayoutParams<MarginLayoutParams> {
                        marginStart =  it
                    }
                }

            }

            is Style.IconBadge -> {
                binding.icon.apply { isVisible = true; setImageDrawable(s.icon) }
                binding.badge.apply {
                    isVisible = true
                    text = s.badge
                }
                (s.badgeMarginEnd ?: badgeMarginStartDefault).let {
                    binding.badge.updateLayoutParams<MarginLayoutParams> {
                        marginStart =  it
                    }
                }
            }
        }
    }
}


sealed class Style{
    data class Icon(val icon:Drawable?):Style()
    data class IconText(val icon:Drawable?,val text:String):Style()
    data class IconTextBadge(val icon:Drawable?,val text:String,val badge:String,val badgeMarginStart:Int? = null):Style()
    data class IconBadge(val icon:Drawable?,val badge:String,val badgeMarginEnd:Int? = null):Style()
}
