package arch.cayenne.module.betslip.ui.viewmodel

import android.icu.util.Currency
import arch.cayenne.lib.common.data.constants.CurrencySymbols
import arch.cayenne.lib.common.ui.viewmodel.NumberCalculatorViewModel
import arch.cayenne.lib.common.utils.ext.SportIntExt.percent

class EarlySettledKeyboardViewModel : NumberCalculatorViewModel() {

    var curencySymbol:String = ""

    /*
    *设置钱标志
    * */
    fun setMoneyCurrency(currency: String){
       curencySymbol = CurrencySymbols.getSymbol(currency)
    }

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