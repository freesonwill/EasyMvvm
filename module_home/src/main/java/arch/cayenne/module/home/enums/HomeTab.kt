package arch.cayenne.module.home.enums

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.ui.fragment.ChampionFragment
import arch.cayenne.module.home.ui.fragment.EarlyFragment
import arch.cayenne.module.home.ui.fragment.TodayFragment

enum class HomeTab(val id: Int, @StringRes val titleRes: Int, val fragment: Fragment?) {
//    ALL(0,R.string.title_all, null),
//    IN_PLAY_ODDS(1,R.string.title_in_play_odds, null),
    TODAY(2,R.string.title_today, TodayFragment()),
    EARLY(3, R.string.title_early, EarlyFragment()),
    CHAMPION(4, R.string.title_champion, ChampionFragment());

    fun getTitle(context: Context): String {
        return context.getString(titleRes)
    }
}

