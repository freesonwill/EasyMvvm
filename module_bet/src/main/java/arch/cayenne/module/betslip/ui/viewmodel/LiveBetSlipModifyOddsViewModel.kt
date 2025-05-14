package arch.cayenne.module.betslip.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import arch.cayenne.lib.base.ui.viewmodel.BaseViewModel
import java.math.BigDecimal

class LiveBetSlipModifyOddsViewModel : BaseViewModel() {
    private val _editNumber: MutableLiveData<String> = MutableLiveData()
    val editNumber: LiveData<String> = _editNumber.distinctUntilChanged()

    /**
     *添加数字，最小到小数点后2位
     * */
    fun addNumber(number: Int) {
        _editNumber.value = _editNumber.value?.let {
            if (it.contains(".")) {
                val lastValue = it.substringAfter(".")
                if (lastValue.length > 2) it
                else "$it$number"
            } else "$it$number"
        } ?: "$number"
    }

    /**
     *值加0.01
     * */
    fun addZeroPointOne() {
        val current = _editNumber.value ?: "0"
        _editNumber.value = BigDecimal(current).add(BigDecimal(0.01)).toDouble().toString()
    }

    /**
     * 添加小数点 如果有小数点则返回
     * */
    fun setDot() {
        _editNumber.value = _editNumber.value?.let {
            if (it.isEmpty()) "0." else if (it.contains(".")) "" else "$it."
        } ?: "0."
    }

    fun clearNumber() {
        _editNumber.value = ""
    }

    /**
     * 删除前一个数字
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

}