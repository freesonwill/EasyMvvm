package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.BankBean

class SelectBankViewModel : BaseViewModel() {

    private val _bankData = MutableLiveData<List<BankBean>>()
    val bankData: LiveData<List<BankBean>> = _bankData

    fun getBankList() {
        val tmp1 = BankBean(0, true, "#", "", "")
        val tmp2 = BankBean(1, false, "#", "中国银行", "")
        val tmp3 = BankBean(2, false, "#", "中国农业银行", "")
        val tmp4 = BankBean(3, false, "#", "中国工商银行", "")
        val tmp5 = BankBean(4, false, "#", "中国建设银行", "")
        val tmp6 = BankBean(5, false, "#", "中国邮政储蓄银行", "")
        val tmp7 = BankBean(6, false, "#", "招商银行", "")
        val tmp8 = BankBean(7, false, "#", "交通银行", "")
        val tmp9 = BankBean(8, false, "#", "中信银行", "")
        val tmp10 = BankBean(9, false, "#", "中国光大银行", "")

        val tmp11 = BankBean(10, true, "A", "", "")
        val tmp12 = BankBean(11, false, "A", "安徽省农村信用社", "")
        _bankData.value = listOf(
            tmp1,
            tmp2,
            tmp3,
            tmp4,
            tmp5,
            tmp6,
            tmp7,
            tmp8,
            tmp9,
            tmp10,
            tmp11,
            tmp12
        )
    }
}