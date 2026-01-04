package com.walisport.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import arch.cayenne.lib.common.utils.ext.DimensionExt.dp2px
import arch.cayenne.lib.common.utils.ext.ResourceExt.getDrawable
import arch.cayenne.lib.common.utils.ext.ResourceExt.getString
import arch.cayenne.module.home.ui.view.NavBarContainer
import arch.cayenne.module.home.ui.view.Style

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import arch.cayenne.module.home.R

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
        assertEquals("com.walisport.app", appContext.packageName)
    }


    @Test
    fun testNavBarContainer() {
        val position = 0
        val context = ApplicationProvider.getApplicationContext<Context>()
        val container = NavBarContainer(context) // 或 mock 出来
        when (position) {
            0 -> {
                val w = container.getWeight(1)
                if (w == 1f) {
                    container.setWeight(1, 80f / 75f)
                    container.setBarStyle(
                        1,
                        Style.IconBadge(
                            R.mipmap.ic_fifa.getDrawable(),
                            "世界杯",
                            (-18f).dp2px.toInt()
                        )
                    )
                } else {
                    container.setBarStyle(
                        1,
                        Style.IconTextBadge(
                            R.drawable.ic_chat.getDrawable(),
                            R.string.title_sport.getString(), "9"
                        )
                    )
                    container.setWeight(1, 1f)
                }
            }

            1 -> {
                val w = container.getWeight(2)
                if (w == 1f) {
                    container.setWeight(2, 121f / 75f)
                    container.setBarStyle(
                        2,
                        Style.Icon(R.mipmap.ic_sport_banner.getDrawable())
                    )
                } else {
                    container.setBarStyle(
                        2,
                        Style.IconTextBadge(
                            R.drawable.ic_chat.getDrawable(),
                            R.string.title_sport.getString(), "9"
                        )
                    )
                    container.setWeight(2, 1f)
                }
            }

            2 -> {
                if (container.getBarStyle(3) == Style.Icon::class.java) {
                    container.setBarStyle(
                        3,
                        Style.IconText(
                            R.drawable.ic_chat.getDrawable(),
                            R.string.title_sport.getString()
                        )
                    )
                } else {
                    container.setBarStyle(3, Style.Icon(R.mipmap.ic_home2.getDrawable()))
                }
            }

            3 -> {

            }
        }
    }
}