package com.example.waterme.ui

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import org.junit.Before
import org.junit.Test
import com.example.waterme.R
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat


@RunWith(AndroidJUnit4::class)
class PlantListFragmentTest{

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
}