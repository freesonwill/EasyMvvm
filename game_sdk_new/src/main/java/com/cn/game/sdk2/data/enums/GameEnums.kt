package com.cn.game.sdk2.data.enums

import com.cn.game.sdk2.R
import com.cn.game.sdk2.data.bean.GamePageBean
import com.cn.game.sdk2.ui.page.fast3.DXDSFragment
import com.cn.game.sdk2.ui.page.fast3.SingleDiceFragment
import com.cn.game.sdk2.ui.page.fast3.SumTotalFragment
import com.cn.game.sdk2.ui.page.fast3.PairsDiceFragment
import com.cn.game.sdk2.ui.page.fast3.LeopardFragment
import com.xcjh.base_lib2.ModuleInitializer

enum class GameEnum(val gameId : Int, val gamePage: List<Game>) {
    /**
     * 默认
     */
    GAME_FAST3(1, Fast3Game.values().toList()),

}

interface Game {
    val gamePageBean: GamePageBean
}

enum class Fast3Game(override val gamePageBean: GamePageBean): Game {
    DEFAULT(GamePageBean(ModuleInitializer.application.getString(R.string.g_home_txt_default)) { DXDSFragment() }),
    SINGLE_DICE(GamePageBean(ModuleInitializer.application.getString(R.string.g_home_tab_single)) { SingleDiceFragment() }),
    SUM_TOTAL(GamePageBean(ModuleInitializer.application.getString(R.string.g_home_tab_sum)) { SumTotalFragment() }),
    PAIRS_DICE(GamePageBean(ModuleInitializer.application.getString(R.string.g_home_tab_sum)) { PairsDiceFragment() }),
    LEOPARD(GamePageBean(ModuleInitializer.application.getString(R.string.g_home_tab_leopard)) { LeopardFragment() }),
}