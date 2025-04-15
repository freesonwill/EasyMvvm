package arch.cayenne.module.home.enums

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import arch.cayenne.module.home.R
import arch.cayenne.module.home.ui.fragment.ChampionFragment
import arch.cayenne.module.home.ui.fragment.EarlyFragment
import arch.cayenne.module.home.ui.fragment.TodayFragment

enum class HomeTab(@StringRes val titleRes: Int, val fragment: Fragment) {
    TODAY(R.string.title_today, TodayFragment()),
    EARLY(R.string.title_early, EarlyFragment()),
    CHAMPION(R.string.title_champion, ChampionFragment());

    fun getTitle(context: Context): String {
        return context.getString(titleRes)
    }
}

