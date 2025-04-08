package com.walisport.module.live.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.walisport.lib.base.data.viewmodel.BaseViewModel

class VideoActivityViewModel : BaseViewModel() {

    private val _url =
        MutableLiveData("http://thinkingform.com/wp-content/uploads/2017/09/video-sample-mp4.mp4?_=1")
    val url: LiveData<String> = _url

    private val _leagueIconUrl = MutableLiveData("")
    val leagueIconUrl: LiveData<String> = _leagueIconUrl

    private val _playerAName = MutableLiveData("法国")
    val playerAName: LiveData<String> = _playerAName

    private val _playerBName = MutableLiveData("阿根廷")
    val playerBName: LiveData<String> = _playerBName
}