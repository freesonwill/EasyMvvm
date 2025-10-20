package com.walisport.module.topup.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.data.entity.PayMoneyBean
import com.walisport.module.topup.data.entity.RechargeMethodBean

class WithdrawFiatViewModel(private val repo: TopUpMainRepository) : BaseViewModel() {

    private val _onRechargeMethodListener = MutableLiveData<List<RechargeMethodBean>>()
    val onRechargeMethodListener: LiveData<List<RechargeMethodBean>> get() = _onRechargeMethodListener

    private val _onPayMoneyListener = MutableLiveData<List<PayMoneyBean>>()
    val onPayMoneyListener: LiveData<List<PayMoneyBean>> get() = _onPayMoneyListener

    fun getPayTypeList() {
        val tmp1 = RechargeMethodBean(0, "提现到EE钱包", isSelect = true, isRecommend = true)
        val tmp2 = RechargeMethodBean(1, "提现到银行卡", isSelect = false, isRecommend = true)
        _onRechargeMethodListener.value = listOf(tmp1, tmp2)

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

    fun selectRechargeType(id: Int) {
        val current = _onRechargeMethodListener.value ?: return
        _onRechargeMethodListener.value = current.map { bean ->
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