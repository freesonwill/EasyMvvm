package com.xcjh.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.xcjh.app.R
import com.xcjh.app.bean.AdvertisementBanner
import com.youth.banner.adapter.BannerAdapter

/**
 * 首页广告Banner适配器
 */
class ImageTitleAdapter(data: MutableList<AdvertisementBanner>?)  : BannerAdapter<AdvertisementBanner, ImageTitleHolder>(data) {
    override fun onCreateHolder(parent: ViewGroup?, viewType: Int): ImageTitleHolder {
        var view = LayoutInflater.from(parent!!.context).inflate(R.layout.banner_image_title, parent, false)
        return ImageTitleHolder(view)
    }

    override fun onBindView(
        holder: ImageTitleHolder?,
        data: AdvertisementBanner?,
        position: Int,
        size: Int) {
        if(data!=null){
            Glide.with(holder!!.itemView)
                .load(data!!.imgUrl) // 替换为您要加载的图片 URL
                 .apply(RequestOptions.bitmapTransform(RoundedCorners(10))) // 设置圆角
                .error(R.drawable.zwt_banner)
                .placeholder(R.drawable.zwt_banner)
                .into(holder.imageView!!)
        }

    }


}


class ImageTitleHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView: AppCompatImageView

    init {
        imageView = view.findViewById<AppCompatImageView>(R.id.imageBanner)

    }
}
