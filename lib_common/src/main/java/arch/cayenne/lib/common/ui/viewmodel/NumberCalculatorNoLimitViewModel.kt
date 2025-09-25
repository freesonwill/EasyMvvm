package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel

open class NumberCalculatorNoLimitViewModel: BaseViewModel() {

    private var _decimalNumber: Int = 2
    val decimalNumber: Int get() = _decimalNumber

    private val _onEditNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() =  _onEditNumber

    val editValue: String get() = _onEditNumber.value.orEmpty()

    fun addNumber(number: Int) {
        val current = onEditNumber.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= decimalNumber) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }
        _onEditNumber.value = newValue
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
        _onEditNumber.value = number.toString()
    }

    fun clearNumber() {
        _onEditNumber.value = ""
    }

    fun doubleNumber() {
        val current = _onEditNumber.value.orEmpty()
        if (current.isEmpty() || current == "0") return
        
        _onEditNumber.value = multiplyByTwo(current)
    }
    
    private fun multiplyByTwo(number: String): String {
        if (number.isEmpty() || number == "0") return "0"
        
        // 分離整數和小數部分
        val parts = number.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""
        
        // 將整個數字當作整數處理（移除小數點）
        val fullNumber = integerPart + decimalPart
        val decimalPlaces = decimalPart.length
        
        // 執行乘法運算
        val result = multiplyStringByTwo(fullNumber)
        
        // 重新插入小數點
        return if (decimalPlaces > 0) {
            if (result.length <= decimalPlaces) {
                "0." + "0".repeat(decimalPlaces - result.length) + result
            } else {
                val intPart = result.substring(0, result.length - decimalPlaces)
                val decPart = result.substring(result.length - decimalPlaces)
                "$intPart.$decPart"
            }
        } else {
            result
        }
    }
    
    private fun multiplyStringByTwo(number: String): String {
        val digits = number.reversed().map { it.digitToInt() }
        val result = mutableListOf<Int>()
        var carry = 0
        
        for (digit in digits) {
            val product = digit * 2 + carry
            result.add(product % 10)
            carry = product / 10
        }
        
        if (carry > 0) {
            result.add(carry)
        }
        
        return result.reversed().joinToString("").trimStart('0').ifEmpty { "0" }
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
}