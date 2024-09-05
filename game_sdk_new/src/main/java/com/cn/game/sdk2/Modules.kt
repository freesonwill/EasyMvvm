package com.cn.game.sdk2

import com.cn.game.sdk2.ui.viewmodel.ChipsViewModel
import com.cn.game.sdk2.ui.viewmodel.EmptyViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.DXDSVm
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3GameHallItemViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.Fast3ViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.LeopardVm
import com.cn.game.sdk2.ui.viewmodel.fast3.PairsDiceVm
import com.cn.game.sdk2.ui.viewmodel.DrawHistoryViewModel
import com.cn.game.sdk2.ui.viewmodel.fast3.SingleDiceVm
import com.cn.game.sdk2.ui.viewmodel.fast3.SumTotalVm
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModules = module {
    viewModel { EmptyViewModel() }
    viewModel { DXDSVm() }
    viewModel { Fast3GameHallItemViewModel() }
    viewModel { Fast3ViewModel() }
    viewModel { LeopardVm() }
    viewModel { PairsDiceVm() }
    viewModel { SingleDiceVm() }
    viewModel { SumTotalVm() }
    viewModel { ChipsViewModel() }
    viewModel { DrawHistoryViewModel() }
}

val moduleList = listOf(viewModules)
