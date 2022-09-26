package com.example.waterme.worker

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.waterme.worker.WaterReminderWorker.Companion.nameKey
import org.hamcrest.Matchers.`is`
import org.junit.Assert.assertThat
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

    @Test
    @Throws(Exception::class)
    fun testSimpleWaterReminderWorker() {
        // Define input data
        val input = workDataOf("plantName" to "Herbicus")

        // Create request
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(15, TimeUnit.MINUTES)
            .setInputData(input)
            .build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver()
        // Enqueue
        workManager.enqueue(request).result.get()
        testDriver?.setPeriodDelayMet(request.id)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData.getString("plantName")
        // Assert
        assertThat(outputData, `is`(input))
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @Test
    fun testFailsIfNoInput() {
        // Define input data

        // Create request
        val request = OneTimeWorkRequestBuilder<WaterReminderWorker>().build()

        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get()
        // Get WorkInfo
        val workInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }

    @Test
    @Throws(Exception::class)
    fun testWithInitialDelay() {
        // Define input data
        val input = workDataOf(nameKey to "")

        // Create request
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(15, TimeUnit.MINUTES)
            .setInputData(input)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(context)
        // Get the TestDriver
        val testDriver = WorkManagerTestInitHelper.getTestDriver()
        // Enqueue
        workManager.enqueue(request).result.get()
        // Tells the WorkManager test framework that initial delays are now met.
        testDriver?.setInitialDelayMet(request.id)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @Test
    @Throws(Exception::class)
    fun testWithConstraints() {
        // Define input data
        val input = workDataOf(nameKey to "Herbicus")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create request
        val request =PeriodicWorkRequestBuilder<WaterReminderWorker>(15, TimeUnit.MINUTES)
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver()
        // Enqueue
        workManager.enqueue(request).result.get()
        // Tells the testing framework that all constraints are met.
        testDriver?.setAllConstraintsMet(request.id)
        // Get WorkInfo and outputData
        val workInfo = workManager.getWorkInfoById(request.id).get()
        val outputData = workInfo.outputData

        // Assert
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }
}
