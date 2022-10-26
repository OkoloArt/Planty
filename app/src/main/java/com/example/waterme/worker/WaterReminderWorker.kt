package com.example.waterme.worker

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.waterme.BaseApplication
import com.example.waterme.R
import com.example.waterme.model.Plants
import com.example.waterme.ui.PlantDetailFragmentArgs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*
import kotlin.random.Random

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(@Assisted context: Context , @Assisted workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    private val notificationId = Random.nextInt(1000)
    private val plants = Plants()

    @SuppressLint("SimpleDateFormat")
    override fun doWork(): Result {

        plants.apply {
            plantImage = inputData.getString(imageKey)
            plantTitle = inputData.getString(nameKey)
            plantDescription = inputData.getString(descriptionKey)
            plantReminderTime = inputData.getIntArray(timeArrayKey)?.toMutableList()
            plantAlarmChoice = inputData.getBoolean(alarmKey, false)
            plantReminderDays = inputData.getStringArray(dayArrayKey)?.toMutableList()
            plantAction = inputData.getStringArray(actionArrayKey)?.toMutableList()

        }
        val args = PlantDetailFragmentArgs(plants).toBundle()

        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setArguments(args)
            .setDestination(R.id.plantDetailFragment)
            .createPendingIntent()

        val calendar = Calendar.getInstance()
        val date = calendar.time
        val formattedDate = SimpleDateFormat("EE", Locale.ENGLISH).format(date.time)


        for (i in plants.plantReminderDays?.indices!!){
            if (formattedDate.equals(plants.plantReminderDays?.get(i))){
                sendNotification(plants.plantTitle!!, plants.plantReminderDays!![i], pendingIntent)
            }
        }

        return Result.success()
    }

    private fun sendNotification(plantName: String, plantDays: String, pendingIntent: PendingIntent) {
        val builder = NotificationCompat.Builder(applicationContext, BaseApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.plants)
            .setContentTitle("Water me!")
            .setContentText("It's time to water your $plantName  $plantDays")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId, builder.build())
        }
    }

    companion object {
        const val imageKey = "IMAGE"
        const val nameKey = "NAME"
        const val descriptionKey = "DESCRIPTION"
        const val timeArrayKey = "TIME"
        const val alarmKey = "ALARM"
        const val dayArrayKey = "DAYS"
        const val actionArrayKey = "ACTIONS"
    }
}