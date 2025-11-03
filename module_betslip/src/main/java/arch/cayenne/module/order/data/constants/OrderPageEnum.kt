package arch.cayenne.module.order.data.constants

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.order.ui.fragment.OrderGameFragment
import arch.cayenne.module.order.ui.fragment.OrderSportFragment

enum class OrderPageEnum(val page: PagerBean) {
    GAME(PagerBean(R.string.title_order_game.getString()) { OrderGameFragment() }),
    SPORT(PagerBean(R.string.title_order_sport.getString()) { OrderSportFragment() })
}