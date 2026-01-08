package arch.cayenne.lib.common.ui.adapter

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
// 示例适配器
class BannerImageAdapter(images: List<String>) : BannerAdapter<String, BannerImageAdapter.Holder>(images) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): Holder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val imageView = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(103.dp2px, 68.dp2px).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                marginEnd = 2.dp2px
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        container.addView(imageView)
        return Holder(container)
    }

    override fun onBindView(holder: Holder, data: String, position: Int, size: Int) {
        Glide.with(holder.imageView).load(data).into(holder.imageView)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(android.R.id.icon) ?:
        (itemView as ViewGroup).getChildAt(0) as ImageView
    }
}