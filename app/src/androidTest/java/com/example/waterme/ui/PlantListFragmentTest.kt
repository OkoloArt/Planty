package com.example.waterme.ui

import android.view.View
import android.widget.Button
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.example.waterme.MainActivityTest
import com.example.waterme.R
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PlantListFragmentTest {

    private lateinit var scenario: FragmentScenario<PlantListFragment>

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        // Create a TestNavHostController
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_WaterMe)
        // scenario.moveToState(newState = Lifecycle.State.STARTED)
    }

    @Test
    fun testNavigationToAddPlantFragment() {

        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        onView(withId(R.id.fab)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.SecondFragment)
    }

    @Test
    fun testViewPagerClick() {
        onView(withId(R.id.view_pager))
            .perform(ViewPagerActions.scrollToPage(0), click())
            .check(matches(withText("Herbicus")))

//        onView(allOf(withId(R.id.view_pager), withParentIndex(0))).perform(click())
//        onView(withId(R.id.dummy_title)).check(matches(withText("Herbicus")))
    }

    @Test
    fun shouldSendNotificationWhichContainsTitleAndText() {
        val uiDevice by lazy { UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()) }


        onView(withId(R.id.view_pager)) .perform(ViewPagerActions.scrollToPage(0), clickOnSendNotification())
        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith(NOTIFICATION_TITLE)),
        LAUNCH_TIMEOUT)
        val title = uiDevice.findObject(By.text(NOTIFICATION_TITLE))
        val text = uiDevice.findObject(By.text(NOTIFICATION_TEXT))
        Assert.assertEquals(NOTIFICATION_TITLE, title.text)
        Assert.assertTrue(text.text.startsWith(NOTIFICATION_TEXT))
        //    title.click()
        //  uiDevice.wait(Until.hasObject(By.text(Espresso.)), LAUNCH_TIMEOUT)

        clearAllNotifications()

    }

    private fun clearAllNotifications() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.openNotification()
        uiDevice.wait(Until.hasObject(By.textStartsWith(NOTIFICATION_TITLE)),
            LAUNCH_TIMEOUT)
        val clearAll: UiObject2 = uiDevice.findObject(By.text(NOTIFICATION_TITLE))
        clearAll.click()
    }

    companion object {
        private const val LAUNCH_TIMEOUT = 5000L
        private const val NOTIFICATION_TITLE="Herbicus"
        private const  val NOTIFICATION_TEXT="Herbicus"
    }

    private fun clickOnSendNotification() : ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Click on the send notification button"
            }

            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(isDisplayed(), isAssignableFrom(Button::class.java))
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<View>(R.id.edit_plant)?.performClick()
            }
        }
    }

}