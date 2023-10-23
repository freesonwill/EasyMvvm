package com.xcjh.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.xcjh.app.R
import com.xcjh.app.bean.AdvertisementBanner
import com.xcjh.app.utils.nice.NiceImageView
import com.xcjh.base_lib.appContext
import com.xcjh.base_lib.utils.dp2px
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
                .transform(RoundedCorners(appContext.dp2px(8))) // 设置圆角半径，单位为像素
                .error(R.drawable.main_banner_load)
                .placeholder(R.drawable.main_banner_load)
                .into(holder.imageView!!)
        }

    }


}


class ImageTitleHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView: NiceImageView

    init {
        imageView = view.findViewById<NiceImageView>(R.id.imageBanner)

    }
}
