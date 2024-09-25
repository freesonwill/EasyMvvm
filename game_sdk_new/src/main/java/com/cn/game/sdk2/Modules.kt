package com.cn.game.sdk2

import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.ui.viewmodel.DrawHistoryViewModel
import com.cn.game.sdk2.ui.viewmodel.DrawResultViewModel
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.ui.viewmodel.MainViewModel
import com.cn.game.sdk2.ui.viewmodel.WinningAnimationViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.GameViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.PairsDiceViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SingleDiceViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SumTotalViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModules = module {
    viewModel { EmptyViewModel() }
    viewModel { DXDSViewModel() }
    viewModel { Fast3GameHallItemViewModel() }
    viewModel { GameViewModel() }
    viewModel { LeopardViewModel() }
    viewModel { PairsDiceViewModel() }
    viewModel { SingleDiceViewModel() }
    viewModel { SumTotalViewModel() }
    viewModel { ChipsViewModel() }
    viewModel { DrawHistoryViewModel() }
    viewModel { DrawResultViewModel() }
    viewModel { WinningAnimationViewModel() }
    viewModel { MainViewModel() }
}

val moduleList = listOf(viewModules)
