package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.entity.PayMethodBean
import com.walisport.module.topup.data.entity.PayMoneyBean

class FiatViewModel : BaseViewModel() {

    private val _onPayMethodListener = MutableLiveData<List<PayMethodBean>>()
    val onPayMethodListener: LiveData<List<PayMethodBean>> get() = _onPayMethodListener

    private val _onPayMoneyListener = MutableLiveData<List<PayMoneyBean>>()
    val onPayMoneyListener: LiveData<List<PayMoneyBean>> get() = _onPayMoneyListener

    fun getPayTypeList() {
        val tmp1 = PayMethodBean(0, "支付宝", isSelect = true, isRecommend = true)
        val tmp2 = PayMethodBean(1, "微信支付", isSelect = false, isRecommend = false)
        val tmp3 = PayMethodBean(2, "EE钱包", isSelect = false, isRecommend = false)
        val tmp4 = PayMethodBean(3, "银行卡", isSelect = false, isRecommend = false)
        val tmp5 = PayMethodBean(4, "人工充值", isSelect = false, isRecommend = false)
        val tmp6 = PayMethodBean(5, "云闪付", isSelect = false, isRecommend = false)
        val tmp7 = PayMethodBean(6, "京东支付", isSelect = false, isRecommend = false)
        val tmp8 = PayMethodBean(7, "银联支付", isSelect = false, isRecommend = false)
        _onPayMethodListener.value = listOf(tmp1, tmp2, tmp3, tmp4, tmp5, tmp6, tmp7, tmp8)

        val tmp10 = PayMoneyBean(0, "¥ 100", isSelect = true, isCustom = false)
        val tmp11 = PayMoneyBean(1, "¥ 200", isSelect = false, isCustom = false)
        val tmp12 = PayMoneyBean(2, "¥ 300", isSelect = false, isCustom = false)
        val tmp13 = PayMoneyBean(3, "¥ 500", isSelect = false, isCustom = false)
        val tmp14 = PayMoneyBean(4, "¥ 1000", isSelect = false, isCustom = false)
        val tmp15 = PayMoneyBean(5, "¥ 2000", isSelect = false, isCustom = false)
        val tmp16 = PayMoneyBean(6, "¥ 3000", isSelect = false, isCustom = false)
        val tmp17 = PayMoneyBean(7, "¥ 5000", isSelect = false, isCustom = false)
        val tmp18 = PayMoneyBean(8, "¥ 10000", isSelect = false, isCustom = false)
        val tmp19 = PayMoneyBean(9, "¥ 30000", isSelect = false, isCustom = false)
        val tmp20 = PayMoneyBean(10, "¥ 40000", isSelect = false, isCustom = false)
        val tmp21 = PayMoneyBean(11, "自定义", isSelect = false, isCustom = true)
        _onPayMoneyListener.value = listOf(
            tmp10,
            tmp11,
            tmp12,
            tmp13,
            tmp14,
            tmp15,
            tmp16,
            tmp17,
            tmp18,
            tmp19,
            tmp20,
            tmp21
        )
    }

    fun selectPayType(id: Int) {
        val current = _onPayMethodListener.value ?: return
        _onPayMethodListener.value = current.map { bean ->
            when (bean.id) {
                id -> bean.copy(isSelect = true)
                else -> bean.copy(isSelect = false)
            }
        }
    }

    fun selectPayMoney(id: Int) {
        val money = _onPayMoneyListener.value ?: return
        _onPayMoneyListener.value = money.map { bean ->
            when (bean.id) {
                id -> bean.copy(isSelect = true)
                else -> bean.copy(isSelect = false)
            }
        }
    }
}