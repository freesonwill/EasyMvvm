package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.IntExt.getMoney
import arch.cayenne.lib.common.utils.ext.StringExt.toValue
import arch.cayenne.module.bet.data.NumberOverEnum

open class NumberCalculatorViewModel : BaseViewModel() {

    private val _onEditNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() =  _onEditNumber


    private val _onNumberLimit = MutableLiveData<Pair<Int, Int>>()
    /***
     * @param first min number
     * @param second max number
     */
    val onNumberLimit: LiveData<Pair<Int, Int>> get() =  _onNumberLimit

    val mixMoney: Int get() = _onNumberLimit.value?.first ?: 0
    val maxMoney: Int get() = _onNumberLimit.value?.second ?: Int.MAX_VALUE

    fun addNumber(number: Int) {
        val current = onEditNumber.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= 2) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }

        setEditNumber(newValue.toValue())
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

    fun setMaxMoney() {
        setEditNumber(maxMoney)
    }

    fun setNumber(number: Int) {
        setEditNumber(number)
    }

    fun clearNumber() {
        _onEditNumber.value = ""
    }

    fun doubleNumber() {
        onEditNumber.value?.let {
            if (it.isEmpty()) {
                ""
            } else {
                val money = if (it.last() == '.') {
                    it.substring(0, it.length - 1)
                } else {
                    it
                }
                setEditNumber(money.toValue() * 2)
            }
        } ?: ""
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

    protected fun setEditNumber(value: Int) {
        _onEditNumber.value = if (value > remainingNumber) {
            remainingNumber.getMoney()
        } else if (value > maxMoney) {
            maxMoney.getMoney()
        } else {
            value.getMoney()
        }
    }

    fun setNumberLimit(min: Int, max: Int) {
        _onNumberLimit.value = Pair(min, max)
    }

    fun setRemainingNumber(number: Int) {
        remainingNumber = number
    }
}