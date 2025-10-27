package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.AddressBean

class AddressViewModel : BaseViewModel() {

    private val _addressData = MutableLiveData<List<AddressBean>>()
    val addressData: LiveData<List<AddressBean>> = _addressData

    fun getAddressList() {
        val tmp1 = AddressBean(
            0,
            "USDT",
            "Tron (TRC20)",
            "TGPs2ZF7nr1cjtdsMQTHmfpgXqqDnAYE6i",
            "（这里是备注）"
        )
        val tmp2 = AddressBean(
            1,
            "BTC",
            "Tron (TRC20)",
            "TGPs2ZF7nr1cjtdsMQTHmfpgXqqDnAYE6i",
            "（这里是备注）"
        )
        val tmp3 = AddressBean(
            2,
            "ETH",
            "Tron (TRC20)",
            "TGPs2ZF7nr1cjtdsMQTHmfpgXqqDnAYE6i",
            "（这里是备注）"
        )
        _addressData.value = listOf(tmp1, tmp2, tmp3)
    }

    fun deleteAddress(address: String) {

    }
}