package com.walisport.lib_database

import androidx.test.platform.app.InstrumentationRegistry
import org.koin.dsl.module

val testRoomModule = module {
    single {
        GameDatabase.invokeTestDatabase(InstrumentationRegistry.getInstrumentation().targetContext)
    }
}

val testModuleList = listOf(
    testRoomModule
)
