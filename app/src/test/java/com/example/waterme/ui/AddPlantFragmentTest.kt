package com.example.waterme.ui

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.waterme.model.Plants
import com.example.waterme.viewmodel.PlantViewModel
import com.google.firebase.FirebaseApp
import org.hamcrest.Matchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(RobolectricTestRunner::class)
class AddPlantFragmentTest {

    @Test
    fun isAdditionCorrect(){

    }
}