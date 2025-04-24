package arch.cayenne.lib.common

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import arch.cayenne.lib.common.utils.ext.DeeplinkExt.deeplink

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
        assertEquals("arch.cayenne.lib_common.test", appContext.packageName)
    }

    @Test
    fun testDeeplink() {
        val uri1 = "http://baidu.com".deeplink("path=hello").also { println("--->$it") }
        assertEquals("http://baidu.com?path=hello", uri1.toString())
        val uri2 = "http://baidu.com".deeplink("path=hello&title=world").also { println("--->$it") }
        assertEquals("http://baidu.com?path=hello&title=world", uri2.toString())
    }
}