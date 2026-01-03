package com.walisport.module.business.common

import android.content.Context
import arch.cayenne.lib.base.data.DefaultInitializer
import com.walisport.module.business.common.data.FavouriteChangedRepository
import com.walisport.module.business.common.data.GameFavouriteRepository
import com.walisport.module.business.common.data.GameRecentRepository
import com.walisport.module.business.common.data.OddsTypeChangedRepository
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

class BusinessCommonModuleInitializer: DefaultInitializer<Unit> {

    override fun create(context: Context) {
        loadKoinModules(moduleList)
    }

    private val viewModules = module {
        
    }

    private val repoModules = module {
        factory {
            GameFavouriteRepository(
                get() ,
                get() ,
                get(named("3n1_http")) ,
                get() ,
                get() ,
                get()
            )
        }

        factory {
            GameRecentRepository(
                get() ,
                get() ,
                get(named("3n1_http")) ,
                get() ,
                get() ,
                get()
            )
        }

        single {
            FavouriteChangedRepository(
            get() ,
        ) }

        single {
            OddsTypeChangedRepository(
                get() ,
            ) }

    }

    private val moduleList: List<Module> = listOf(viewModules, repoModules)
}