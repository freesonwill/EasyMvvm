package com.cn.game.sdk2.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.data.enums.ChipBean
import com.cn.game.sdk2.data.enums.ChipsEnum
import com.cn.game.sdk2.websocket.gameAboutModel

class ChipsViewModel : ViewModel() {

    /**
     * 投注的钱
     */
    private val _chipsList = MutableLiveData<List<ChipBean>>()
    val chipsList: LiveData<List<ChipBean>> = _chipsList

    init {
        _chipsList.value =
            ChipsEnum.createChipsList().apply {
            val tempMoney = gameAboutModel.tempBalance.value ?: 0
            if (tempMoney >= first().chip.money) {
                first().isSelected = true
                userLastSelectedChip = first()
            }
        }
    }

    private val firstChip: ChipBean
        get() = chipsList.value!!.first()

    val currentChip: ChipBean?
        get() = chipsList.value!!.firstOrNull { it.isSelected }

    val currentChipIndex: Int
        get() = chipsList.value!!.indexOfFirst { it.isSelected }

    private var userLastSelectedChip: ChipBean

    private fun setSelectedChip(ce: ChipsEnum?) {
        _chipsList.value = chipsList.value?.let { list ->
            ce?.let {
                list.onEach {
                    it.isSelected = it.chip == ce
                }
            } ?: list.onEach {
                it.isSelected = false
            }
        }
    }

    private fun setSelectedChip(cb: ChipBean) {
        setSelectedChip(cb.chip)
    }

    fun setUserSelectChip(cb: ChipBean) {
        userLastSelectedChip = cb
        setSelectedChip(cb)
    }

    fun cancelBet() {
        val money = gameAboutModel.tempBalance.value ?: 0
        Log.d("abcd", "++++++ $money")
        if (money < userLastSelectedChip.chip.money) {
            setMaxPossibleBetChip(money)
        } else {
            setSelectedChip(userLastSelectedChip)
        }
    }

    /***
     * 显示最大可下注筹码
     */
    private fun setMaxPossibleBetChip(money: Long) {
        val list = chipsList.value ?: return
        if (money < firstChip.chip.money) {
            setDisableChip()
            return
        }
        val maxChip = list.filter { it.chip.money <= money }.maxByOrNull { it.chip.money }
        if (maxChip != null) {
            setSelectedChip(maxChip)
        } else {
            setDisableChip()
        }
    }

    fun refresh() {
        val money = gameAboutModel.tempBalance.value ?: 0
        val selectBean = chipsList.value?.firstOrNull { it.isSelected }
        if (selectBean != null) {
            if (selectBean.chip.money > money) {
                setMaxPossibleBetChip(money)
            } else {
                setSelectedChip(selectBean)
            }
        } else {
            setMaxPossibleBetChip(money)
        }
    }

    private fun setDisableChip() {
        setSelectedChip(null)
    }
}