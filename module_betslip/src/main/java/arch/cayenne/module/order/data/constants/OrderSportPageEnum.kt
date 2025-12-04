package arch.cayenne.module.order.data.constants

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.order.ui.fragment.OrderSportPageFragment

enum class OrderSportPageEnum(val value: Int, val page: PagerBean) {
    UNSETTLED(3, PagerBean(R.string.unsettled.getString(), { OrderSportPageFragment() })),
    SETTLED(4, PagerBean(R.string.title_order_sport_settled.getString(), { OrderSportPageFragment() })),
    RESERVE(5, PagerBean(R.string.title_order_sport_reserve.getString(), { OrderSportPageFragment() }))
}