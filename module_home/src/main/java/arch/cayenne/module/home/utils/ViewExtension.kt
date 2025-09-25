package arch.cayenne.module.home.utils

import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import arch.cayenne.module.home.R
import com.google.android.material.tabs.TabLayout

fun ImageView.setFavoriteIcon(selected: Boolean, force: Boolean) {
    val layers = this.drawable as LayerDrawable
    val unselected = layers.findDrawableByLayerId(R.id.background)
    val selectedDrawable = layers.findDrawableByLayerId(R.id.foreground)

    // 做 alpha 淡入淡出動畫
    val fadeIn = ObjectAnimator.ofInt(
        selectedDrawable,
        "alpha",
        if (selected) 0 else 255,
        if (selected) 255 else 0
    )
    val fadeOut = ObjectAnimator.ofInt(
        unselected,
        "alpha",
        if (selected) 255 else 0,
        if (selected) 0 else 255
    )

    fadeIn.duration = if (force) 0 else 150
    fadeOut.duration = if (force) 0 else 150

    fadeIn.start()
    fadeOut.start()
    this.isSelected = selected
}


/**
 * 執行 TabLayout 滾動到指定位置(不帶動畫)
 * */
fun TabLayout.scrollToPositionWithoutAnim(position: Int) {
    if (position == -1) return
    TabLayout::class.java
        .getDeclaredMethod(
            "setScrollPosition",
            Int::class.java,
            Float::class.java,
            Boolean::class.java,
            Boolean::class.java
        ).apply {
            isAccessible = true
            invoke(this@scrollToPositionWithoutAnim, position, 0f, true, false)
        }
}