package arch.cayenne.module.handicap

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import arch.cayenne.module.handicap.ui.viewmodel.HandicapViewModel
import arch.cayenne.module.handicap.ui.viewmodel.SimulateViewModel
import arch.cayenne.module.handicap.ui.viewmodel.HandicapBigSmallViewModel
import arch.cayenne.module.handicap.ui.viewmodel.HandicapCornerViewModel
import arch.cayenne.module.handicap.ui.viewmodel.HandicapLetBallViewModel

class HandicapModuleInitializer : DefaultInitializer<Unit> {

    override fun create(context: Context) {
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        includes(autoViewModels)
        viewModelOf(::SimulateViewModel)
        viewModelOf(::HandicapCornerViewModel)
        viewModelOf(::HandicapLetBallViewModel)
        viewModelOf(::HandicapBigSmallViewModel)
    }

    private val repoModules = module {

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}