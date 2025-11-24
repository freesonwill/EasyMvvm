package arch.cayenne.module.order.data.constants

import arch.cayenne.lib.base.data.model.PagerBean
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.betslip.R
import arch.cayenne.module.order.ui.fragment.GameOrderAllFragment
import arch.cayenne.module.order.ui.fragment.GameBonusFragment
import arch.cayenne.module.order.ui.fragment.GameMultipleFragment

enum class GamePageEnum(val page: PagerBean) {
    ALL(PagerBean(R.string.title_multiple.getString()) { GameOrderAllFragment() }),
    MULTIPLE(PagerBean(R.string.title_multiple.getString()) { GameMultipleFragment() }),
    BONUS(PagerBean(R.string.title_multiple.getString()) { GameBonusFragment() })
}