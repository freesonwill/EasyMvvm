package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.callback.livedata.UnPeekLiveData

class Fast3GameHallItemViewModel : ViewModel() {
    private val _hallItems = UnPeekLiveData<List<GameHallItem>>()
    val hallItems: LiveData<List<GameHallItem>> = _hallItems
    fun setGameType(type: Int) {
        _hallItems.value = gameAboutModel.moreGames.value?.filter { it.gameType == type }
    }
}