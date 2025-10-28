package com.walisport.module.topup

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.topup.data.TopUpMainRepository
import com.walisport.module.topup.ui.viewmodel.BankCardViewModel
import com.walisport.module.topup.ui.viewmodel.FundDetailsViewModel
import com.walisport.module.topup.ui.viewmodel.RealTimeCashBackViewModel
import com.walisport.module.topup.ui.viewmodel.TopUpViewModel
import com.walisport.module.topup.ui.viewmodel.CryptoViewModel
import com.walisport.module.topup.ui.viewmodel.FiatViewModel
import com.walisport.module.topup.ui.viewmodel.AddressViewModel
import com.walisport.module.topup.ui.viewmodel.AddAddressViewModel
import com.walisport.module.topup.ui.viewmodel.BetDetailViewModel
import com.walisport.module.topup.ui.viewmodel.WithdrawFiatViewModel
import com.walisport.module.topup.ui.viewmodel.WithdrawViewModel
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
        viewModelOf(::TopUpViewModel)
        viewModelOf(::BankCardViewModel)
        viewModelOf(::WithdrawViewModel)
        viewModelOf(::AddAddressViewModel)
        viewModelOf(::CryptoViewModel)
        viewModelOf(::FiatViewModel)
        viewModelOf(::AddressViewModel)
        viewModelOf(::BetDetailViewModel)
        viewModelOf(::WithdrawFiatViewModel)
        viewModelOf(::FundDetailsViewModel)
        viewModelOf(::RealTimeCashBackViewModel)
    }

    private val repoModules = module {
        factoryOf(::TopUpMainRepository)
    }

    private val managerModule = module {
        factoryOf(::TopUpRemoteManager)
    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules, managerModule)
}