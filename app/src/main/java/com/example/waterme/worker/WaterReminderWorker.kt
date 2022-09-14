package com.example.waterme.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.waterme.BaseApplication
import com.example.waterme.MainActivity
import com.example.waterme.R
import kotlin.random.Random

class WaterReminderWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val notificationId = Random.nextInt(1000)

    override fun doWork(): Result {
        val plantName = inputData.getString(nameKey)
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(applicationContext, BaseApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.plants)
            .setContentTitle("Water me!")
            .setContentText("It's time to water your $plantName")
            .setContentIntent(pendingIntent)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }


        return Result.success()
    }

    companion object {
        const val nameKey = "NAME"
    }
}