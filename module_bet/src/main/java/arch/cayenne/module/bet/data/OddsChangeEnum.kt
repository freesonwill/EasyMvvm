package arch.cayenne.module.bet.data

import arch.cayenne.module.bet.R

enum class OddsChangeEnum(val value: Int, val textRes: Int) {
    ANY(0, R.string.title_odds_change_any),
    BETTER(1, R.string.title_odds_change_better),
    NO_CHANGE(2, R.string.title_odds_change_none),
}