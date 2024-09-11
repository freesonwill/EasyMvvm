package com.cn.game.sdk2.ui.viewmodel.fast3

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.data.bean.GameHallItem
import com.cn.game.sdk2.websocket.gameAboutModel
import com.xcjh.base_lib2.callback.livedata.UnPeekLiveData

class Fast3GameHallItemViewModel : ViewModel() {
    private lateinit var _hallItems:LiveData<List<GameHallItem>>
    val hallItems: LiveData<List<GameHallItem>> get() = _hallItems

    fun setGameType(type: Int) {
        _hallItems = Transformations.map(gameAboutModel.moreGames){
            val list = gameAboutModel.moreGames.value?.filter { it.gameType == type }
            list
        }
    }
}