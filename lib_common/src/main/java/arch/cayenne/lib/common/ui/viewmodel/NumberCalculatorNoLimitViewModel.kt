package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.math.BigDecimal
import java.math.RoundingMode

open class NumberCalculatorNoLimitViewModel : BaseViewModel() {

    private var _decimalNumber: Int = 2
    val decimalNumber: Int get() = _decimalNumber

    protected val _onEditNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() = _onEditNumber

    val editValue: String get() = _onEditNumber.value.orEmpty()

    open fun addNumber(number: Int) {
        val current = onEditNumber.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= decimalNumber) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }
        setEditNumber(newValue)
    }

    fun setDot() {
        _onEditNumber.value = _onEditNumber.value?.let {
            if (!it.contains(".")) {
                "$it."
            } else {
                it
            }
        } ?: "0."
    }

    fun setNumber(number: Long) {
        setEditNumber(number.toString())
    }

    fun setNumber(number: String) {
        setEditNumber(number)
    }

    fun clearNumber() {
        _onEditNumber.value = ""
    }

    fun doubleNumber() {
        val current = _onEditNumber.value.orEmpty()
        if (current.isEmpty() || current == "0") return

        val v = BigDecimal(current).multiply(BigDecimal(2))
        val finalResult = v.setScale(
            2,
            RoundingMode.DOWN
        )
        setEditNumber(finalResult.stripTrailingZeros().toPlainString())
    }

    fun backNumber() {
        _onEditNumber.value = _onEditNumber.value?.let {
            if (it.length > 1) {
                it.substring(0, it.length - 1)
            } else {
                ""
            }
        }
    }

    protected open fun setEditNumber(value: String) {
        _onEditNumber.value = value
    }

    fun setDecimalNumber(decimal: Int) {
        _decimalNumber = decimal
    }
}