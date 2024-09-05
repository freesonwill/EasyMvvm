package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cn.game.sdk2.utils.ext.DensityExt.dp2px
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.xcjh.base_lib2.base.BaseViewModel

class DrawHistoryViewModel: BaseViewModel() {

    // 開獎歷史資料
    private val _drawHistories = MutableLiveData<List<RoundInfoBean>>()
    val drawHistories: LiveData<List<RoundInfoBean>> = _drawHistories

    // 是否為展開的狀態
    var isExpand = false

    // 目前View的高度
    var currentHeight = 42.dp2px

    // recyclerView的高度
    var resultRvHeight = -1

    // 展開需移動的高度
    var resultAnimMoveHeight = -1

    /**
     * 設定開獎歷史資料
     *
     * @param histories 開獎歷史資料
     */
    fun setDrawHistories(histories: List<RoundInfoBean>) {
        _drawHistories.value = histories
    }
}