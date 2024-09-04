package com.cn.game.sdk2.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.cn.game.sdk2.data.bean.SelectAnnotationBean

class ChipsViewModel : ViewModel() {

    private val defaultIndex = 0
    /**
     * 投注的钱
     */
    val chipsList: List<SelectAnnotationBean> by lazy {
        listOf(
            SelectAnnotationBean(money = 1000, true),
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
        )
    }

    val currentChip: SelectAnnotationBean
        get() = chipsList.first { it.select }

    val currentChipIndex: Int
        get() = chipsList.indexOfFirst { it.select }

    fun setSelectedChip(money: Int) {
        chipsList.forEach {
            it.select = it.money == money
            return@forEach
        }
    }

    fun setSelectedChip(chip: SelectAnnotationBean) {
        setSelectedChip(chip.money)
    }

    fun reset() {
        chipsList.forEachIndexed { index, selectAnnotationBean ->
            selectAnnotationBean.select = index == defaultIndex
        }
    }
}