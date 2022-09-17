package com.example.waterme.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.waterme.model.Plants
import com.example.waterme.ui.getOrAwaitValue
import com.google.firebase.FirebaseApp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock


@RunWith(AndroidJUnit4::class)
class PlantViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var plantViewModel: PlantViewModel
    private var plantList = arrayListOf<Plants>()

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        plantViewModel = PlantViewModel(ApplicationProvider.getApplicationContext())

    }

    @Test
    fun getPlants() {
    }

    @Test
    fun addPlants() {
        val plants = Plants("", "", "Carrot")
        plantViewModel.addPlants(plants)
        val value = plantViewModel.plants.getOrAwaitValue()
        addPlants(value)
        assertEquals(plantList[0].plantTitle, "Carrot")
    }

    @Test
    fun getRealTimeUpdate() {
    }

    @Test
    fun updatePlant() {
    }

    @Test
    fun deletePlant() {
    }

    private fun addPlants(plants: Plants) {
        if (!plantList.contains(plants)) {
            plantList.add(plants)
        } else {
            if (plants.isDeleted) {
                plantList.remove(plants)
            }
        }
    }
}