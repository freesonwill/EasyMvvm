package com.cn.game.sdk2.websocket.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameAboutModel : ViewModel() {
    enum class Stage {
        NEW, DEAL, SETTLE
    }

    private val _currentStage = MutableLiveData<Stage>()

    val currentStage: LiveData<Stage>
        get() = _currentStage

    fun changeStage(stage: Stage) {
        _currentStage.value = stage
    }

    var miniGameId: Int = 0
    var countDown: Int = 0
    var roundId: String = ""
}