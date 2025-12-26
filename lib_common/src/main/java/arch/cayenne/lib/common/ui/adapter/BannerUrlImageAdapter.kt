package arch.cayenne.lib.common.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter

// 示例适配器
class BannerUrlImageAdapter(private val images: List<Pair<String, Int>>) :
    BannerAdapter<Pair<String, Int>, BannerUrlImageAdapter.Holder>(images) {

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): Holder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val imageView = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        container.addView(imageView)
        return Holder(container)
    }

    override fun onBindView(holder: Holder, data: Pair<String, Int>, position: Int, size: Int) {
        Glide.with(holder.imageView).load(data.first).placeholder(data.second)
            .into(holder.imageView)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(android.R.id.icon)
            ?: (itemView as ViewGroup).getChildAt(0) as ImageView
    }
}