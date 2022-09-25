package com.example.waterme.worker

import android.content.Context
import android.util.Log
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.waterme.worker.WaterReminderWorker.Companion.nameKey
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class WaterReminderWorkerTest {

    private lateinit var context: Context
    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }
//
//    @Test
//    fun testWaterReminderWorker() {
//        val inputData = workDataOf("ResponseCode" to 200)
//        val worker = TestListenableWorkerBuilder<WaterReminderWorker>(context).build()
//
//        // Start the work synchronously
//        val result = worker.startWork().get()
//
//        assertThat(result, `is`(ListenableWorker.Result.success()))
//    }

    @Test
    @Throws(Exception::class)
    fun testSimpleEchoWorker() {
        // Define input data
        val input = workDataOf( nameKey to "Herbicus")

        // Create Constraints
        val constraints = Constraints.Builder()
            // Add network constraint.
            .setRequiredNetworkType(NetworkType.CONNECTED)
            // Add battery constraint.
            .setRequiresBatteryNotLow(true)
            .build()

        // Create request
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(15, TimeUnit.MINUTES)
            .setInputData(input)
            .build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver()
        // Enqueue
        workManager.enqueue(request).result.get()
        // Tells the testing framework that all constraints are met.
        testDriver?.setAllConstraintsMet(request.id)
        testDriver?.setPeriodDelayMet(request.id)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))

    }
    }
