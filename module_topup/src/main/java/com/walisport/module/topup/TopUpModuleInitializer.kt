package com.walisport.module.topup

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.topup.data.TopUpDetailRepository
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.data.TopUpRecordsRepository
import com.walisport.module.topup.ui.viewmodel.TopUpDetailViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpRecordsViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpViewModel
import com.walisport.module.topup.ui.viewmodel.WalletViewModel
import com.walisport.module.topup.ui.viewmodel.WithdrawDetailViewModel
import com.walisport.module.topup.ui.viewmodel.WithdrawRecordsViewModel
import com.walisport.module.topup.ui.viewmodel.WithdrawViewModel
import com.walisport.module.topup.ui.viewmodel.SportPickerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class TopUpModuleInitializer : DefaultInitializer<String> {

    private val TAG = this.javaClass.simpleName

    override fun create(context: Context): String {
        loadKoinModules(moduleList)
        return TAG
    }

    private val viewModules = module {
        viewModelOf(::WalletViewModel)
        viewModelOf(::TopUpViewModel)
        viewModelOf(::TopUpDetailViewModel)
        viewModelOf(::TopUpRecordsViewModel)
        viewModelOf(::WithdrawViewModel)
        viewModelOf(::WithdrawDetailViewModel)
        viewModelOf(::WithdrawRecordsViewModel)
        viewModelOf(::SportPickerViewModel)
    }

    private val repoModules = module {
        factoryOf(::TopUpMainRepository)
        factoryOf(::TopUpRecordsRepository)
        factoryOf(::TopUpDetailRepository)
    }

    private val managerModule = module {
        factoryOf(::TopUpRemoteManager)

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}