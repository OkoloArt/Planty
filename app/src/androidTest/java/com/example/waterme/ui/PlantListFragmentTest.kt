package com.example.waterme.ui

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.ViewPagerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.waterme.R
import com.google.common.truth.Truth.assertThat
import com.google.firebase.FirebaseApp
import org.hamcrest.Matchers.allOf
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

}