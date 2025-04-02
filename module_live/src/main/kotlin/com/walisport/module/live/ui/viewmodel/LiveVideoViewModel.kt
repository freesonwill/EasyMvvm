package com.walisport.module.live.ui.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.walisport.lib_base.data.viewmodel.BaseViewModel

class LiveVideoViewModel : BaseViewModel() {

    val videoPlayVisible = MutableLiveData(View.VISIBLE)

    val statusVisible = MutableLiveData(View.INVISIBLE)

    val playerAUrl = MutableLiveData<String>("")
    val playerAName = MutableLiveData<String>("")

    val playerBUrl = MutableLiveData<String>("")
    val playerBName = MutableLiveData<String>("")


    val titleText = MutableLiveData<String>("")
    val titleTextColor = MutableLiveData<Int>(com.walisport.lib_common.R.color.white)
    val titleTextSize = MutableLiveData<Int>(com.walisport.lib_common.R.dimen.sp_17)

    val subTitleText = MutableLiveData<String>("")
    val subTitleTextColor = MutableLiveData<Int>(com.walisport.lib_common.R.color.color_929298)
    val subTitleTextSize = MutableLiveData<Int>(com.walisport.lib_common.R.dimen.sp_14)

}