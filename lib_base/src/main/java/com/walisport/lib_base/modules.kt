package com.walisport.lib_base

import com.walisport.lib_base.data.viewmodel.EmptyViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModules = module {
    viewModel { EmptyViewModel() }
}

val moduleList:List<Module> = listOf(viewModules)