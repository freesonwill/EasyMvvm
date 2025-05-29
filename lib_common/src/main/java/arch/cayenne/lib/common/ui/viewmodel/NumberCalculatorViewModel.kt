package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import arch.cayenne.lib.common.data.constants.NumberOverEnum
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoney
open class NumberCalculatorViewModel : BaseViewModel() {

    private val _onEditNumber = MutableLiveData("")
    val onEditNumber: LiveData<String> get() =  _onEditNumber

    val editValue: String get() = _onEditNumber.value.orEmpty()

    private val _onNumberLimit = MutableLiveData<Pair<Long, Long>>(Pair(0, 0))
    /***
     * @param first min number
     * @param second max number
     */
    val onNumberLimit: LiveData<Pair<Long, Long>> get() =  _onNumberLimit

    private val _onOverNumberListener = MutableLiveData(NumberOverEnum.DEFAULT)
    val onOverNumberListener: LiveData<NumberOverEnum> get() = _onOverNumberListener

    val mixMoney: Long get() = onNumberLimit.value?.first ?: 0
    val maxMoney: Long get() = onNumberLimit.value?.second ?: Long.MAX_VALUE
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
        _onEditNumber.value = if (value > maxMoney) {
            setOverNumberListener(NumberOverEnum.OVER_MAX)
            maxMoney.getMoney()
        } else if (value > remainingNumber) {
            setOverNumberListener(NumberOverEnum.OVER_REMAINING)
            remainingNumber.getMoney()
        } else {
            value.getMoney()
        }
        _onOverNumberListener.value = NumberOverEnum.DEFAULT
    }

    fun setNumberLimit(min: Long, max: Long) {
        if (min > max) {
            _onNumberLimit.value = Pair(max, min)
        } else {
            _onNumberLimit.value = Pair(min, max)
        }
    }

    fun setRemainingNumber(number: Long) {
        remainingNumber = number
    }

    protected fun setOverNumberListener(value: NumberOverEnum) {
        _onOverNumberListener.value = value
        _onOverNumberListener.value = NumberOverEnum.DEFAULT
    }
}