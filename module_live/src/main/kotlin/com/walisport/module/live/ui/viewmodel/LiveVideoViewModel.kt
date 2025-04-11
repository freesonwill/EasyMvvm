package com.walisport.module.live.ui.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.walisport.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.VideoSourceBean

class LiveVideoViewModel : BaseViewModel() {

    val liveUrl =
        MutableLiveData("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

    val videoPlayVisible = MutableLiveData(View.VISIBLE)

    val statusVisible = MutableLiveData(View.INVISIBLE)

    val playerAUrl = MutableLiveData<String>("")
    val playerAName = MutableLiveData<String>("")

    val playerBUrl = MutableLiveData<String>("")
    val playerBName = MutableLiveData<String>("")


    val titleText = MutableLiveData<String>("")
    val titleTextColor = MutableLiveData<Int>(com.walisport.lib.common.R.color.white)
    val titleTextSize = MutableLiveData<Int>(com.walisport.lib.common.R.dimen.sp_17)

    val subTitleText = MutableLiveData<String>("")
    val subTitleTextColor = MutableLiveData<Int>(com.walisport.lib.common.R.color.color_929298)
    val subTitleTextSize = MutableLiveData<Int>(com.walisport.lib.common.R.dimen.sp_14)


    val sources =
        MutableLiveData(
            listOf(
                VideoSourceBean(
                    sources = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    thumb = "https://peach.blender.org/wp-content/uploads/bbb-splash.png",
                    title = "Big Buck Bunny",
                    subTitle = "By Blender Foundation"
                ),
                VideoSourceBean(
                    sources = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    thumb = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e4/Elephants_Dream_cover.jpg/1200px-Elephants_Dream_cover.jpg",
                    title = "Elephant Dream",
                    subTitle = "By Blender Foundation"
                ),
                VideoSourceBean(
                    sources = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    thumb = "https://d6u22qyv3ngwz.cloudfront.net/ad/76Ab/google-chromecast-bigger-blazes-small-7.jpg",
                    title = "For Bigger Blazes",
                    subTitle = "By Google"
                ),
                VideoSourceBean(
                    sources = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                    thumb = "https://i.ytimg.com/vi/DAUKV5zf7Qk/hqdefault.jpg",
                    title = "For Bigger Fun",
                    subTitle = "By Google"
                )
            )
        )

}