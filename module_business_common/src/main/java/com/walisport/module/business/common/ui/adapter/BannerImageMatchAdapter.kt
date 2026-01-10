package com.walisport.module.business.common.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink
import arch.cayenne.lib.common.utils.ext.clickNoRepeat
import com.bumptech.glide.Glide
import com.youth.banner.adapter.BannerAdapter

// 示例适配器
class BannerImageMatchAdapter(
    private val images: List<ImageData>,
    private val onItemClick: ((v:View,data: ImageData) -> Unit)? = { v, data ->
        val url = data.targetUrl
        val navController = v.findFragment<Fragment>().findNavController()
        navController.navigate(arch.cayenne.lib.res.R.string.nav_module_web_fragment.deeplink("url" to url))
    }
) : BannerAdapter<BannerImageMatchAdapter.ImageData, BannerImageMatchAdapter.Holder>(images) {
    data class ImageData(
        val imgUrl: String,
        val targetUrl: String,
    )
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): Holder {
        val container = FrameLayout(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val imageView = ImageView(parent.context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        container.addView(imageView)
        return Holder(container)
    }

    override fun onBindView(holder: Holder, data: ImageData, position: Int, size: Int) {
        Glide.with(holder.imageView).load(data.imgUrl).into(holder.imageView)
        holder.itemView.clickNoRepeat {
            onItemClick?.invoke(it,data)
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(android.R.id.icon) ?:
        (itemView as ViewGroup).getChildAt(0) as ImageView
    }
}