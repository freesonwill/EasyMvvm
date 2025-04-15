package com.walisport.module.live.ui.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import com.walisport.module.live.data.model.VideoSourceBean

class LiveVideoViewModel : BaseViewModel() {

    val liveUrl =
        MutableLiveData("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")

    val videoPlayVisible = MutableLiveData(View.VISIBLE)

    val statusVisible = MutableLiveData(View.INVISIBLE)

    val playerAUrl = MutableLiveData<String>("")

    val playerBUrl = MutableLiveData<String>("")


    val titleText = MutableLiveData<String>("")
    val titleTextColor = MutableLiveData<Int>(arch.cayenne.lib.common.R.color.white)
    val titleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_17)

    val subTitleText = MutableLiveData<String>("")
    val subTitleTextColor = MutableLiveData<Int>(arch.cayenne.lib.res.R.color.color_929298)
    val subTitleTextSize = MutableLiveData<Int>(arch.cayenne.lib.common.R.dimen.sp_14)

    private val _url =
        MutableLiveData("http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4?_=1")
    val url: LiveData<String> = _url

    private val _leagueIconUrl = MutableLiveData("")
    val leagueIconUrl: LiveData<String> = _leagueIconUrl

    private val _playerAName = MutableLiveData("法国")
    val playerAName: LiveData<String> = _playerAName

    private val _playerBName = MutableLiveData("阿根廷")
    val playerBName: LiveData<String> = _playerBName


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

    fun setUrl(url: String) {
        _url.value = url
    }

}