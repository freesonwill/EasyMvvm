package com.xcjh.base_lib.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class StringFormatUtil {
    companion object{
        val decimalFormatMax2 : DecimalFormat = DecimalFormat("#0.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH))
    }
}