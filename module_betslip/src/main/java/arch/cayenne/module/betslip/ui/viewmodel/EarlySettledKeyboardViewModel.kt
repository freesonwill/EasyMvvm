package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.common.utils.ext.SportIntExt.percent

class EarlySettledKeyboardViewModel : NumberCalculatorViewModel() {

    fun setAmountMoney(betAmount: Long, minAmount: Long) {
        setNumberLimit(minAmount, betAmount)
        setRemainingNumber(betAmount)
        setNumber(betAmount)
    }

    /**
     * 结算金额百分比
     * */
    fun setPercentNumber(percent: Int) {
        val value = maxMoney.percent(percent)
        setNumber(value)
    }
}