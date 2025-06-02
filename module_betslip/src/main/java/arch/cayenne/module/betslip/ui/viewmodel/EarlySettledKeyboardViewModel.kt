package arch.cayenne.module.betslip.ui.viewmodel

import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.common.utils.ext.SportIntExt.percent

class EarlySettledKeyboardViewModel : NumberCalculatorViewModel() {

    companion object {
        private const val MIX_LIMIT = 1L
    }

    override var decimalNumber: Int = 3

    fun setAmountMoney(betAmount: Long) {
        setNumberLimit(MIX_LIMIT, betAmount)
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