package com.xcjh.base_lib.utils

import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class StringFormatUtil {
    companion object{
        val decimalFormatMax2 : DecimalFormat = DecimalFormat("#0.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
        val decimalFormat2 : DecimalFormat = DecimalFormat("#0.00", DecimalFormatSymbols.getInstance(Locale.ENGLISH))

    }

    init {
        decimalFormatMax2.roundingMode = RoundingMode.FLOOR
        decimalFormat2.roundingMode = RoundingMode.FLOOR
    }
}