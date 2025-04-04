package com.walisport.module.live.viewmodel

import androidx.lifecycle.MutableLiveData
import com.walisport.lib.base.data.viewmodel.BaseViewModel

class VideoActivityViewModel : BaseViewModel() {

    val url =
        MutableLiveData("http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4?_=1")

    val leagueIconUrl = MutableLiveData("")

    val playerAName = MutableLiveData("法国")
    val playerBName = MutableLiveData("阿根廷")
}