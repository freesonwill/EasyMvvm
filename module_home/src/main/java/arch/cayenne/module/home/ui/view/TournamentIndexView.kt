package arch.cayenne.module.home.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.module.home.R

class TournamentIndexView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val letters = mutableListOf<Char>()
    private val viewMap = mutableMapOf<Char, View>()
    var onLetterTouch: ((Char) -> Unit)? = null
    private var lastTouchedIndex = -1
    private var selectedLetter: Char? = null

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    fun setLetters(newLetters: List<Char>) {
        letters.clear()
        letters.addAll(newLetters)
        buildViews()
    }

    fun setSelectedLetter(letter: Char?) {
        selectedLetter = letter
        updateSelection()
    }

    private fun buildViews() {
        removeAllViews()
        viewMap.clear()

        letters.forEach { letter ->
            val view = if (letter == '*') {
                ImageView(context).apply {
                    layoutParams = LayoutParams(24.dp2px, 18.dp2px)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            } else {
                TextView(context).apply {
                    layoutParams = LayoutParams(24.dp2px, 18.dp2px)
                    text = letter.toString()
                    textSize = 11f
                    gravity = Gravity.CENTER
                }
            }
            viewMap[letter] = view
            addView(view)
        }
        updateSelection()
    }

    private fun updateSelection() {
        letters.forEach { letter ->
            val view = viewMap[letter]
            val isSelected = letter == selectedLetter
            when (view) {
                is ImageView -> view.setImageResource(
                    if (isSelected) R.drawable.ic_hot_league_index
                    else R.drawable.ic_hot_league_index_unselect
                )

                is TextView -> view.setTextColor(
                    SkinnableResourceManager.getColor(
                        context,
                        if (isSelected) arch.cayenne.lib.common.R.color.brand_color
                        else R.color.brand_color_index_unselect
                    )
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index = ((event.y / height) * letters.size).toInt().coerceIn(0, letters.size - 1)
        val letter = letters.getOrNull(index) ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (index != lastTouchedIndex) {
                    lastTouchedIndex = index
                    onLetterTouch?.invoke(letter)
                    setSelectedLetter(letter)
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                lastTouchedIndex = -1
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

