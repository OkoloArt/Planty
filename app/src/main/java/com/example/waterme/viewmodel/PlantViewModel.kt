package com.example.waterme.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.waterme.NODE_PLANTS
import com.example.waterme.model.Plants
import com.example.waterme.worker.WaterReminderWorker
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(application: Application, private val workManager: WorkManager) : ViewModel() {

    private val dbPlants = FirebaseDatabase.getInstance().getReference(NODE_PLANTS)
 //   private val workManager = WorkManager.getInstance(application)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _plants = MutableLiveData<Plants>()
    val plants: LiveData<Plants> get() = _plants

    private val _currentPlant = MutableLiveData<Plants>()
    val currentPlant: LiveData<Plants> get() = _currentPlant


    fun addPlants(plants: Plants) {
        plants.plantId = dbPlants.push().key

        dbPlants.child(plants.plantId!!).setValue(plants).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun setCurrent(plants: Plants?) {
        _currentPlant.value = plants!!
    }


    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            _plants.value = plants!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            _plants.value = plants!!
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val plants = snapshot.getValue(Plants::class.java)
            plants?.plantId = snapshot.key
            plants?.isDeleted = true
            _plants.value = plants!!
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    fun getRealTimeUpdate() {
        dbPlants.addChildEventListener(childEventListener)
    }

    fun isUserInputValid(
        plantName: String,
        plantImage: String,
        days: MutableList<String>,
        action: MutableList<String>,
        time: MutableList<Int>,
    ): Boolean {
        if (plantName.isBlank() || plantImage.isBlank() || days.isEmpty() || action.isEmpty() || time.isEmpty()) {
            return false
        }
        return true
    }

    fun updatePlant(plants: Plants) {
        dbPlants.child(plants.plantId!!).setValue(plants).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun deletePlant(plants: Plants) {
        dbPlants.child(plants.plantId!!).setValue(null).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    internal fun scheduleReminder(
        duration: Long,
        unit: TimeUnit,
        plants: Plants,
    ) {
        // TODO: create a Data instance with the plantName passed to it
        val data = createInputData(plants)

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

       // TODO: Generate a OneTimeWorkRequest with the passed in duration, time unit, and data instance
        val request =
            PeriodicWorkRequestBuilder<WaterReminderWorker>(24, TimeUnit.HOURS, 5, TimeUnit.MINUTES)
                .setConstraints(constraint)
                .setInputData(data)
                .setInitialDelay(duration, unit)
                .build()

        // TODO: Enqueue the request as a unique work request
        workManager.enqueue(request)

    }

    private fun createInputData(plants: Plants): Data {
        val data = Data.Builder()
        data.putString(WaterReminderWorker.imageKey, plants.plantImage)
        data.putString(WaterReminderWorker.nameKey, plants.plantTitle)
        data.putString(WaterReminderWorker.descriptionKey, plants.plantDescription)
        data.putBoolean(WaterReminderWorker.alarmKey, plants.plantAlarmChoice!!)
        plants.plantReminderTime?.let {
            data.putIntArray(WaterReminderWorker.timeArrayKey,
                it.toIntArray())
        }
        plants.plantReminderDays?.let {
            data.putStringArray(WaterReminderWorker.dayArrayKey,
                it.toTypedArray())
        }
        plants.plantAction?.let {
            data.putStringArray(WaterReminderWorker.actionArrayKey,
                it.toTypedArray())
        }
        return data.build()
    }

    override fun onCleared() {
        super.onCleared()
        dbPlants.removeEventListener(childEventListener)
    }
}