package com.walisport.lib.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class DatabaseTest {

    private val gameDB: GameDatabase by inject(GameDatabase::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun insert() {
        runBlocking {
//            launch {
//                gameDB.testDao().insert(TestBean(1, "test"))
//            }.join()
//            val data = async {
//                gameDB.testDao().getAll()
//            }.await()
//            println("data $data")
        }
    }

    @After
    fun finish() {
        println("Done")
    }
}