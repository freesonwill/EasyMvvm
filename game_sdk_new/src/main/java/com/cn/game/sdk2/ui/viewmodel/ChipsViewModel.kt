package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.data.bean.SelectAnnotationBean
import com.cn.game.sdk2.websocket.gameAboutModel

class ChipsViewModel : ViewModel() {

    /**
     * 投注的钱
     */
    private val _chipsList = MutableLiveData<List<SelectAnnotationBean>>()
    val chipsList: LiveData<List<SelectAnnotationBean>> = _chipsList

    init {
        _chipsList.value = listOf(
            SelectAnnotationBean(money = 1000),
            SelectAnnotationBean(money = 2000),
            SelectAnnotationBean(money = 5000),
            SelectAnnotationBean(money = 10000),
            SelectAnnotationBean(money = 20000),
            SelectAnnotationBean(money = 50000),
            SelectAnnotationBean(money = 100000),
            SelectAnnotationBean(money = 200000),
            SelectAnnotationBean(money = 500000),
            SelectAnnotationBean(money = 1000000),
            SelectAnnotationBean(money = 2000000),
            SelectAnnotationBean(money = 5000000),
            SelectAnnotationBean(money = 10000000),
        ).apply {
            val tempMoney = gameAboutModel.tempBalance.value ?: 0
            if (tempMoney >= first().money) {
                first().select = true
            }
        }
    }

    private val firstChip: SelectAnnotationBean
        get() = chipsList.value!!.first()

    val currentChip: SelectAnnotationBean
        get() = chipsList.value!!.first { it.select }

    val currentChipIndex: Int
        get() = chipsList.value!!.indexOfFirst { it.select }

    fun setSelectedChip(money: Int) {
        val list = chipsList.value ?: return
        list.forEach {
            it.select = it.money == money
        }
        _chipsList.value = list
    }

    fun setSelectedChip(chip: SelectAnnotationBean) {
        setSelectedChip(chip.money)
    }

    /***
     * 显示最大可下注筹码
     */
    private fun showMaxPossibleBetChip(money: Long) {
        val list = chipsList.value ?: return
        val maxChip = list.filter { it.money <= money }.maxByOrNull { it.money }
        if (maxChip != null) {
            setSelectedChip(maxChip)
        } else {
            setSelectedChip(list.first())
        }
    }

    fun refresh() {
        val money = gameAboutModel.tempBalance.value ?: 0
        val selectBean = chipsList.value?.firstOrNull { it.select }
        if (selectBean != null) {
            if (selectBean.money > money) {
                showMaxPossibleBetChip(money)
            } else {
                backUserLastSelectChip(selectBean, money)
            }
        } else {
            if (currentChip.money <= money) {
                backUserLastSelectChip(null, money)
            } else {
                if (firstChip.money <= money) {
                    setSelectedChip(firstChip)
                }
            }
        }
    }

    /**
     * 取消下注筹码判断是否需要选中用户最近一次手选筹码
     */
    private fun backUserLastSelectChip(betteBean: SelectAnnotationBean?, money: Long) {
        if (betteBean == currentChip || betteBean == null) return
        if (currentChip.money > money) return
       setSelectedChip(betteBean.money)
    }
}