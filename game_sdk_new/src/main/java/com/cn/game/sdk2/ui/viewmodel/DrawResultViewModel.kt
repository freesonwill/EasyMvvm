package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cn.game.sdk2.websocket.bean.RoundInfoBean
import com.xcjh.base_lib2.base.BaseViewModel

class DrawResultViewModel: BaseViewModel() {

    // 當局結果
    private val _drawResult = MutableLiveData<RoundInfoBean>()
    val drawResult: LiveData<RoundInfoBean> = _drawResult

    /**
     * 設定當局開獎結果
     *
     * @param result 開獎結果
     */
    fun setDrawResult(result: RoundInfoBean) {
        _drawResult.value = result
    }

}