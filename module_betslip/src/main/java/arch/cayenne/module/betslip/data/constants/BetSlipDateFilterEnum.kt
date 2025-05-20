package arch.cayenne.module.betslip.data.constants

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import java.util.Calendar

enum class BetSlipDateFilterEnum(val title: String): DateSelectListener {
    ALL(R.string.date_picker_all.getString()) {
        override fun startTime(): Long? {
            return null
        }

        override fun endTime(): Long? {
            return null
        }
    },
    TODAY(R.string.date_picker_today.getString()) {
        override fun startTime(): Long {
            return getToday()
        }

        override fun endTime(): Long {
            return System.currentTimeMillis()
        }
    },
    YESTERDAY(R.string.date_picker_yesterday.getString()) {
        override fun startTime(): Long {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            c.add(Calendar.DAY_OF_MONTH, -1)
            return c.timeInMillis
        }

        override fun endTime(): Long {
            return getToday() - 1
        }
    },
    IN_SEVEN_DAYS(R.string.date_picker_in_7_day.getString()) {
        override fun startTime(): Long {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            c.add(Calendar.DAY_OF_MONTH, -7)
            return c.timeInMillis
        }

        override fun endTime(): Long {
            return System.currentTimeMillis()
        }
    },
    CUSTOM(R.string.date_picker_custom.getString()) {
        override fun startTime(): Long? {
            return null
        }

        override fun endTime(): Long? {
            return null
        }
    };

    companion object {
        fun getToday(): Long {
            val c = Calendar.getInstance()
            c.set(Calendar.HOUR_OF_DAY, 0)
            c.set(Calendar.MINUTE, 0)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            return c.timeInMillis
        }
    }
}

interface DateSelectListener {
    fun startTime(): Long?
    fun endTime(): Long?
}