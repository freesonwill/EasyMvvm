package arch.cayenne.module.home.ui.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.skin.res.SkinnableResourceManager
import arch.cayenne.lib.skin.widget.SkinnableImageView
import com.bumptech.glide.Glide

data class PromoTab(
    val imageResId: Int? = null,
    val onClick: (() -> Unit)? = null
) {
    fun createView(context: Context): View {
        return SkinnableImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(104.dp2px, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            background = SkinnableResourceManager.getDrawable(context, android.R.color.transparent)

            imageResId?.let { resId ->
                Glide.with(context).load(resId).into(this)
            }
        }
    }
}


