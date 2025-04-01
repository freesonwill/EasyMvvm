package com.walisport.module.live.ui.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.walisport.lib_base.data.viewmodel.BaseViewModel

class LiveVideoViewModel : BaseViewModel() {

    val videoPlayVisible = MutableLiveData(View.VISIBLE)
}