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
class BannerImageAdapter(private val images: List<Int>) : BannerAdapter<Int, BannerImageAdapter.Holder>(images) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): Holder {
        // 1. 根布局必须是 match_parent！！
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // 2. 再往里面放一个你想要的 108x68 的 ImageView
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

    override fun onBindView(holder: Holder, data: Int, position: Int, size: Int) {
        holder.imageView.setImageResource(data)
        // Glide.with(holder.imageView).load(data).into(holder.imageView)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(android.R.id.icon) ?:
        (itemView as ViewGroup).getChildAt(0) as ImageView
    }
}