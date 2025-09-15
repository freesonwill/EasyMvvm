package arch.cayenne.module.bet.data

import arch.cayenne.module.bet.R

enum class OddsChangeEnum(val value: Int, val textRes: Int, val toastRes: Int? = null) {
    ANY(2, R.string.title_odds_change_any),
    BETTER(1, R.string.title_odds_change_better, R.string.toast_odds_change_better),
    NO_CHANGE(0, R.string.title_odds_change_none, R.string.toast_odds_change_none);

    companion object {
        fun fromValue(value: Int): OddsChangeEnum {
            return entries.firstOrNull { it.value == value } ?: ANY
        }
    }
}