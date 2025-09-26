package arch.cayenne.lib.common.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import arch.cayenne.lib.common.data.constants.NumberOverEnum
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoney
import arch.cayenne.lib.common.utils.ext.SportIntExt.getMoneyForScale
import arch.cayenne.lib.common.utils.ext.SportStringExt.toMoneyForScale

open class NumberCalculatorViewModel : NumberCalculatorNoLimitViewModel() {

    private val _onNumberLimit = MutableLiveData<Pair<Long, Long>>(Pair(0, 0))
    val onNumberLimit: LiveData<Pair<Long, Long>> get() =  _onNumberLimit

    private val _onOverNumberListener = MutableLiveData(NumberOverEnum.DEFAULT)
    val onOverNumberListener: LiveData<NumberOverEnum> get() = _onOverNumberListener

    val minMoney: Long get() = onNumberLimit.value?.first ?: 0
    val maxMoney: Long get() = onNumberLimit.value?.second ?: Long.MAX_VALUE
    private var remainingNumber: Long? = null
    private var maxNumber: Long? = null

    fun setMaxMoney() {
        setEditNumber(maxMoney.getMoney())
    }

    override fun setEditNumber(value: String) {
        if (remainingNumber == null && maxNumber == null) {
            super.setEditNumber(value)
            return
        }
        val rn = remainingNumber ?: return
        val mn = maxNumber ?: return
        val mV = value.toMoneyForScale(decimalNumber)
        _onEditNumber.value = if (mV > mn) {
            if (maxMoney > rn) {
                setOverNumberListener(NumberOverEnum.OVER_REMAINING)
                rn.getMoneyForScale(decimalNumber)
            } else {
                setOverNumberListener(getMaxToast())
                mn.getMoneyForScale(decimalNumber)
            }
        } else if (mV > rn) {
            if (rn < mn) {
                setOverNumberListener(NumberOverEnum.OVER_REMAINING)
                rn.getMoneyForScale(decimalNumber)
            } else {
                setOverNumberListener(getMaxToast())
                mn.getMoneyForScale(decimalNumber)
            }
        } else if (mV == 0L) {
            getZero()
        } else {
            mV.getMoneyForScale(decimalNumber)
        }
        _onOverNumberListener.value = NumberOverEnum.DEFAULT
    }

    private fun getZero(): String {
        val lastValue = _onEditNumber.value
        return if (lastValue.isNullOrEmpty()) {
            "0"
        } else {
            if (!lastValue.startsWith("0.")) return lastValue

            val decimalPart = lastValue.substringAfter("0.")
            // 如果小數部分長度 >= scale，維持原字串
            if (decimalPart.length >= decimalNumber) {
                lastValue
            } else {
                // 補足到 scale 位數
                "0." + decimalPart.padEnd(decimalPart.length + 1, '0')
            }
        }
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

    protected fun setOverNumberListener(value: NumberOverEnum?) {
        value?.let {
            _onOverNumberListener.value = value
            _onOverNumberListener.value = NumberOverEnum.DEFAULT
        }
    }

    fun setMaxNumber(number: Long) {
        maxNumber = number
    }

    open fun getMaxToast():NumberOverEnum?{
        return NumberOverEnum.OVER_MAX
    }
}