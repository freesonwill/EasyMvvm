package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.forEachIndexed
import androidx.core.view.updateLayoutParams
import arch.cayenne.lib.skin.widget.SkinnableLinearLayout
import arch.cayenne.module.home.R

/**
 * @date: 2025/10/3 14:22
 * @description:
 */
class NavBarContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SkinnableLinearLayout(context, attrs, defStyleAttr) {
    private var selectedIndex: Int = -1

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.NavBarContainerStyle,
            defStyleAttr,
            0
        ).let { ta ->
            try {
                selectedIndex = ta.getInt(R.styleable.NavBarContainerStyle_selected_index, -1)
            } finally {
                ta.recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onInit()
    }

    private fun onInit() {
        setSelected(selectedIndex)
    }

    fun setSelected(index: Int) {
        forEachIndexed { i, v ->
            v.isSelected = i == index
        }
        this.selectedIndex = index
    }

    fun setOnItemSelectedListener(listener: ((parent: NavBarContainer, view: View, position: Int) -> Unit)? = null) {
        forEachIndexed { index, view ->
            view.setOnClickListener {
                listener?.invoke(this, view, index)
            }
        }
    }

    fun setWeight(index: Int, weight: Float) {
        getChildAt(index)?.let {
            it.updateLayoutParams<LayoutParams> {
                this.weight = weight
            }
        }
    }

    fun getWeight(index: Int): Float {
        return (getChildAt(index).layoutParams as LayoutParams).weight
    }

    fun setBarStyle(index: Int, style: Style) {
        (getChildAt(index) as NavBarView?)?.setBarStyle(style)
    }

    fun getBarStyle(index: Int):Class<out Style> = (getChildAt(index) as NavBarView).getBarStyle()
}