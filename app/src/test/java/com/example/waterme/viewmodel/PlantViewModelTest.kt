package com.example.waterme.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.impl.utils.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
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

    @Before
    fun setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        plantViewModel = PlantViewModel(ApplicationProvider.getApplicationContext())

    }

    @Test
    fun setCurrentPlants() {
        val plants = Plants("", "", "Carrot")
        plantViewModel.setCurrent(plants)
        val value = plantViewModel.plants.getOrAwaitValue()
        assertEquals(value.plantTitle, "Carrot")
    }
}