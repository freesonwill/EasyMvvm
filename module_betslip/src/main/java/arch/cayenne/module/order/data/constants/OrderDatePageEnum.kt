package arch.cayenne.module.order.data.constants

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.order.ui.fragment.OrderDateCustomFragment
import arch.cayenne.module.order.ui.fragment.OrderDateSelectorFragment

enum class OrderDatePageEnum(val page: PagerBean) {
    DATE(PagerBean(R.string.title_order_date_selector.getString()) { OrderDateSelectorFragment() }),
    CUSTOM(PagerBean(R.string.title_order_date_custom.getString()) { OrderDateCustomFragment() })
}