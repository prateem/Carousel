package com.meetarp.imagecarousel

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4

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
        assertEquals("com.prateemshrestha.imagecarousel", appContext.packageName)
    }

    @Test
    fun inflationSuccessful() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val carousel = ImageCarousel(appContext)
        assertEquals("onFinishInflate should have added the component layout.", 1, carousel.childCount)
    }
}
