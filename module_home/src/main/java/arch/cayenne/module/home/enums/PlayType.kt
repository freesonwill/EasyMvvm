package arch.cayenne.module.home.enums

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import arch.cayenne.module.home.R

enum class PlayType(val id: Int, @StringRes val titleRes: Int) {
//    ALL(0,R.string.title_all, null),
//    IN_PLAY_ODDS(1,R.string.title_in_play_odds, null),
    TODAY(2,R.string.title_today),
    EARLY(3, R.string.title_early),
    CHAMPION(4, R.string.title_champion);

    fun getTitle(context: Context): String {
        return context.getString(titleRes)
    }
}

