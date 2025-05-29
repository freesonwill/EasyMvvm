package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.module.betslip.utisl.BetSlipUtils.toBigDecimal
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.math.BigDecimal
import java.math.RoundingMode


class EarlySettledKeyboardViewModel : BaseViewModel() {
    private val _editNumber: MutableLiveData<String> = MutableLiveData()
    private val repository: arch.cayenne.module.betslip.data.repo.BetSlipRepository by inject { parametersOf(viewModelScope) }
    val earlySettlePriceLiveData: MutableLiveData<Double> = MutableLiveData(0.0)
    val editNumber: LiveData<String> = _editNumber.distinctUntilChanged()
    var betId: String = ""

    fun setArguments(betId: String, betAmount: Double) {
        this.betId = betId
        earlySettlePriceLiveData.value = betAmount

    }

    /**
     * 添加数字，小于小数点后2位
     * */
    fun addNumber(number: Int) {
        val current = _editNumber.value ?: ""
        if (current.contains(".")) {
            val lastValue = current.substringAfter(".")
            if (lastValue.length > 2) return
        }
        val value = current + number
        maxLimitValue(value.toDouble())
    }

    fun setNumber(number: Int) {
        maxLimitValue(number.toDouble())
    }

    /**
     * 结算金额百分比
     * */
    fun setPercentNumber(percent: Double) {
        val value =
            toBigDecimal(earlySettlePriceLiveData.value.toString()).multiply(toBigDecimal(percent.toString()))
                .setScale(2, RoundingMode.HALF_UP).toDouble()
        maxLimitValue(value)
    }

    /**
     *添加小数点
     * */
    fun setDot() {
        val current = _editNumber.value ?: ""
        if (current.isEmpty()) {
            _editNumber.value = "0."
            return
        } else if (current.contains(".")) {
            return
        }
        _editNumber.value = "$current."
    }

    fun clearNumber() {
        _editNumber.value = ""
    }

    fun doubleNumber() {
        _editNumber.value?.let {
            if (it.isEmpty()) {
                ""
            } else {
                _editNumber.value = BigDecimal(it).multiply(BigDecimal(2)).toString()
            }
        } ?: ""
    }

    /**
     * 清除数字
     * */
    fun backNumber() {
        _editNumber.value = _editNumber.value?.let {
            if (it.length > 1) {
                it.substring(0, it.length - 1)
            } else {
                ""
            }
        }
    }

    /**
     * 输入的数字不能超过结算金额
     * */
    private fun maxLimitValue(value: Double) {
        if (value > (earlySettlePriceLiveData.value ?: 0.0)) {
            return
        }
        _editNumber.value = value.toString()
    }


}