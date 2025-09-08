package arch.cayenne.module.home.utils

import android.animation.ObjectAnimator
import android.graphics.drawable.LayerDrawable
import android.widget.ImageView
import arch.cayenne.module.home.R

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