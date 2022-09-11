package com.example.waterme.ui

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.waterme.R
import com.example.waterme.model.Plants
import com.google.android.material.chip.Chip
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import org.hamcrest.Matchers.*
import org.hamcrest.text.IsEmptyString
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddPlantFragmentTest{
    private lateinit var scenario: FragmentScenario<AddPlantFragment>

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        // Create a TestNavHostController
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_WaterMe)
        // scenario.moveToState(newState = Lifecycle.State.STARTED)
    }

    @Test
    fun testNavigationToPlantListFragment() {

        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())

        scenario.onFragment { fragment ->
            // Set the graph on the TestNavHostController
            navController.setGraph(R.navigation.nav_graph)

            // Make the NavController available via the findNavController() APIs
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        onView(ViewMatchers.withId(R.id.button_second)).perform(ViewActions.click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.FirstFragment)
    }

    @Test
    fun testEditTextEmptyString(){
    }

    @Test
    fun chipContainsText() {
        onView(allOf(withText(containsString("Mon")), isAssignableFrom(Chip::class.java))).check(matches(isDisplayed()))
    }

    @Test
    fun checkTimePickerVisibility_and_setTimePicker(){
        onView(withId(R.id.timePicker)).check(matches(isDisplayed()))
        onView(withId(R.id.timePicker)).perform(PickerActions.setTime(15,30))
    }

    @Test
    fun checkSwitch_and_setSwitch(){
        onView(withId(R.id.alarm_switch)).check(matches(isChecked()))
    }
}