package com.example.myapplication

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.myapplication", appContext.packageName)
    }

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val scope = CoroutineScope(Dispatchers.Main + exceptionHandler)

    @Test
    fun addition_isCorrect() {
        scope.launch {
            // 新的协程任务
            println("New coroutine is running0")
        }
        // 尝试启动一个可能会抛出异常的协程
        scope.launch {
            // 一些可能会抛出异常的操作
            throw NullPointerException("Something went wrong")
        }

        scope.launch {
            // 新的协程任务
            println("New coroutine is running1")
        }
    }


    //val scope = CoroutineScope(SupervisorJob() +Dispatchers.Main + exceptionHandler)

}