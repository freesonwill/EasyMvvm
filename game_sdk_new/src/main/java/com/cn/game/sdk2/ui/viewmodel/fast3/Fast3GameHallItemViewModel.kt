package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.LiveData
import com.cn.game.sdk2.data.bean.GameHallItem
import com.xcjh.base_lib.base.BaseViewModel
import com.xcjh.base_lib.callback.livedata.UnPeekLiveData

class Fast3GameHallItemViewModel : BaseViewModel() {
    private val _hallItems = UnPeekLiveData<List<GameHallItem>>()
    val hallItems:LiveData<List<GameHallItem>> = _hallItems

    override fun onInit() {
        val list = mutableListOf<GameHallItem>()
        for (i in 0..10) {
            list.add(GameHallItem("a", "快三", "3389在线"))
        }
        _hallItems.value = list
    }
}