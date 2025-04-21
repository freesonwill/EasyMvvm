package arch.cayenne.module.bet.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.data.viewmodel.BaseViewModel
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
import arch.cayenne.module.bet.data.NumberOverEnum

open class NumberCalculatorViewModel : BaseViewModel() {

    private val _onEditNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() =  _onEditNumber


    private val _onNumberLimit = MutableLiveData<Pair<Long, Long>>()
    /***
     * @param first min number
     * @param second max number
     */
    val onNumberLimit: LiveData<Pair<Long, Long>> get() =  _onNumberLimit

    private val _onOverNumberListener = MutableLiveData(NumberOverEnum.DEFAULT)
    val onOverNumberListener: LiveData<NumberOverEnum> get() = _onOverNumberListener

    private val mixMoney: Long get() = onNumberLimit.value?.first ?: 0
    private val maxMoney: Long get() = onNumberLimit.value?.second ?: Long.MAX_VALUE
    private var remainingNumber: Long = Long.MAX_VALUE

    fun addNumber(number: Int) {
        val current = onEditNumber.value.orEmpty()

        val newValue = if (current.contains('.')) {
            val decimalPart = current.substringAfter('.', "")
            if (decimalPart.length >= 2) return  // 最多兩位小數，直接返回不修改
            current + number
        } else {
            current + number
        }

        setEditNumber(newValue.toMoney())
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

    fun setNumber(number: Long) {
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
                setEditNumber(money.toMoney() * 2)
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

    protected fun setEditNumber(value: Long) {
        _onEditNumber.value = if (value > remainingNumber) {
            _onOverNumberListener.value = NumberOverEnum.OVER_REMAINING
            remainingNumber.getMoney()
        } else if (value > maxMoney) {
            _onOverNumberListener.value = NumberOverEnum.OVER_MAX
            maxMoney.getMoney()
        } else {
            value.getMoney()
        }
        _onOverNumberListener.value = NumberOverEnum.DEFAULT
    }

    fun setNumberLimit(min: Long, max: Long) {
        _onNumberLimit.value = Pair(min, max)
    }

    fun setRemainingNumber(number: Long) {
        remainingNumber = number
    }
}