package arch.cayenne.module.bet.data

import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.bet.R

enum class NumberOverEnum(val msg: String? = null) {
    DEFAULT,
    OVER_REMAINING(R.string.toast_over_remaining.getString()), // 超過餘額上限
    OVER_MAX(R.string.toast_over_max.getString()), // 超過最大上限
}