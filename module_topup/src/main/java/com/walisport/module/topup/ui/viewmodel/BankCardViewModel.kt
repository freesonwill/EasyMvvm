package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.BankCardBean

class BankCardViewModel : BaseViewModel() {

    private val _cardData = MutableLiveData<List<BankCardBean>>()
    val cardData: LiveData<List<BankCardBean>> = _cardData

    fun getMyBankCardList() {
        val tmp1 = BankCardBean(0, "中国银行", "", "0923 1232 1233 1234", "#B62939", false)
        val tmp2 = BankCardBean(1, "杭州银行", "", "0923 1232 1233 1234", "#2A8FBF", false)
        val tmp3 = BankCardBean(2, "邮政银行", "", "0923 1232 1233 1234", "#60AB34", false)
        val tmp4 = BankCardBean(3, "宁波银行", "", "0923 1232 1233 1234", "#D59826", true)
        _cardData.value = listOf(tmp1, tmp2, tmp3, tmp4)
    }

    fun deleteBankCard(id: Int) {
        val list = _cardData.value!!.toMutableList().filter { it.id != id }
        _cardData.value = list
    }

    fun selectBankCard(id: Int) {
        val list = _cardData.value!!.toMutableList()
        list.forEach { item ->
            item.isSelected = item.id == id
        }
        _cardData.value = list
    }
}