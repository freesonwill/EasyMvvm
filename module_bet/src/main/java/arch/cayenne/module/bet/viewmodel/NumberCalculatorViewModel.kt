package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel

open class NumberCalculatorViewModel : BaseViewModel() {

    protected val _onEdidNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() =  _onEdidNumber

    /***
     * @param first min number
     * @param second max number
     */
    protected val _onNumberLimit = MutableLiveData<Pair<Int, Int>>()
    val onNumberLimit: LiveData<Pair<Int, Int>> get() =  _onNumberLimit

    val mixMoney: Int get() = _onNumberLimit.value?.first ?: 0
    val maxMoney: Int get() = _onNumberLimit.value?.second ?: Int.MAX_VALUE

    fun addNumber(number: Int) {
        val current = _onEdidNumber.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= 2) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }

        val formatted = formatMoney(newValue)
        val limited = if (formatted.toDouble() > maxMoney) maxMoney.toString() else formatted

        _onEdidNumber.value = limited
    }

    fun setDot() {
        _onEdidNumber.value = _onEdidNumber.value?.let {
            if (!it.contains(".")) {
                "$it."
            } else {
                it
            }
        } ?: "0."
    }

    fun setMaxMoney() {
        _onEdidNumber.value = maxMoney.toString()
    }

    fun setNumber(number: Int) {
        if (number > maxMoney) {
            setMaxMoney()
        } else {
            _onEdidNumber.value = number.toString()
        }
    }

    fun setNumber(number: Float) {
        if (number > maxMoney) {
            setMaxMoney()
        } else {
            _onEdidNumber.value = number.toString()
        }
    }

    fun clearNumber() {
        _onEdidNumber.value = ""
    }

    fun doubleNumber() {
        _onEdidNumber.value = _onEdidNumber.value?.let {
            if (it.isEmpty()) {
                ""
            } else {
                val money = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                val doubledValue = money.toDouble() * 2
                val formattedValue = formatMoney(doubledValue.toString())
                if (doubledValue > maxMoney) maxMoney.toString() else formattedValue
            }
        } ?: ""
    }

    fun backNumber() {
        _onEdidNumber.value = _onEdidNumber.value?.let {
            if (it.length > 1) {
                it.substring(0, it.length - 1)
            } else {
                ""
            }
        }
    }

    private fun formatMoney(value: String): String {
        val regex = "^\\d*(\\.\\d{0,2})?$".toRegex()
        return if (regex.matches(value)) {
            value
        } else {
            value.toDoubleOrNull()?.let {
                "%.2f".format(it)
            } ?: ""
        }
    }

    fun setNumberLimit(min: Int, max: Int) {
        _onNumberLimit.value = Pair(min, max)
    }
}