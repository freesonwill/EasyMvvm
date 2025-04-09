package com.walisport.module.home.enums

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.walisport.module.home.R
import com.walisport.module_home.ui.fragment.ChampionFragment
import com.walisport.module_home.ui.fragment.EarlyFragment
import com.walisport.module_home.ui.fragment.TodayFragment

enum class HomeTab(@StringRes val titleRes: Int, val fragment: Fragment) {
    TODAY(R.string.title_today, TodayFragment()),
    EARLY(R.string.title_early, EarlyFragment()),
    CHAMPION(R.string.title_champion, ChampionFragment());

    fun getTitle(context: Context): String {
        return context.getString(titleRes)
    }
}

